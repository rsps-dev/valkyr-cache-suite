/**
 * 
 */
package store.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import store.plugin.extension.ConfigExtensionBase;
import store.plugin.extension.FXController;
import store.plugin.extension.LoaderExtensionBase;
import store.progress.AbstractProgressListener;
import suite.Constants;
import suite.annotation.PluginDescriptor;
import suite.controller.Selection;
import utility.StringUtilities;

/**
 * @author ReverendDread Sep 14, 2019
 */
@Slf4j
public class PluginManager {
	 
	public static PluginManager instance;

	@Getter private boolean loading;
	@Getter private final List<Plugin> plugins = Lists.newArrayList();
	private PluginClassLoader pluginClassLoader;

	private PluginManager(AbstractProgressListener progressListener, boolean reinitialize) {
		instance = this;
		if (reinitialize) {
			plugins.clear();
			loadPlugins();
		}
	}

	private void loadPlugins() {
		if (isLoading()) {
			return;
		}
		loading = true;
		Set<Path> paths = findPlugins();
		try {
			List<URL> pluginPaths = paths.stream().map(path -> {
				try {
					return path.toUri().toURL();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				return null;
			}).collect(Collectors.toList());
			Plugin plugin = null;
			int loaded = 0, length = pluginPaths.size();
			try {
				for (URL url : pluginPaths) {

					//log.info("loading:{}", url.toString());
					pluginClassLoader = new PluginClassLoader(url);
					JarFile jar = new JarFile(url.getPath());
					Enumeration<JarEntry> entries = jar.entries();
					List<URL> fxmlFiles = Lists.newArrayList();
					//log.info("{}", getClasseNames(url.getPath()));

					while (entries.hasMoreElements()) {
						
						JarEntry next = entries.nextElement();

						if (next.isDirectory()) {
							continue;
						}

						if (next.getName().endsWith(".class")) {

							String classpath = next.getName();
							classpath = classpath.replace("/", ".");
							classpath = classpath.replace(".class", "");

							try {

								Class<?> clazz = pluginClassLoader.loadClass(classpath);

								if (Modifier.isAbstract(clazz.getModifiers())) {
									//log.info("Skipping {}", next.getName());
									continue;
								}

								if (clazz.isAnnotationPresent(PluginDescriptor.class)) {
									plugin = (Plugin) clazz.newInstance();
									plugin.setClassLoader(pluginClassLoader);
								}

							} catch(Exception ex){
								ex.printStackTrace();
								log.info("Failed to load class {}", classpath);
							}
							
						} else if(next.getName().endsWith(".fxml")){

							String classpath = next.getName();
							classpath = classpath.replace("/", ".");

							fxmlFiles.add(pluginClassLoader.getResource(classpath));

						}

					}

					final PluginType type = plugin.getDescriptor().type();

					if (Constants.LEGACY_MODE && type != PluginType.ITEM && type != PluginType.MESH) {
						continue;
					}

					plugin.setFxmlFiles(fxmlFiles);
					plugins.add(plugin);

					float progress = (loaded / (float) length);
					Selection.progressListener.notify(progress, "Loading plugin - " + StringUtilities.getFileName(url.getFile()));

					if (plugin.load()) {
						//log.info("progress {}, load {}, size {}, file {}", progress, loaded, pluginPaths.size(), StringUtilities.getFileName(url.getFile()));
						loaded++;
					} else {
						log.info("Error loading plugin {}", plugin.getClass().getAnnotation(PluginDescriptor.class).type().name());
						plugins.remove(plugin);
						length--;
					}
					
					jar.close();
					
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				log.error("Failed to load plugins", e1.getCause().fillInStackTrace());
				loading = false;
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Failed to load plugins", ex.getCause().fillInStackTrace());
			loading = false;
		}

		Selection.progressListener.finish(null, null);
		loading = false;

		log.info("Loaded: {} plugins", plugins.size());
	}

	private Set<Path> findPlugins() {
		try {
			Set<Path> set = Sets.newHashSet();
			Path root = Files.createDirectories(Paths.get(Constants.DEFAULT_SAVE_DIR));
			if(Constants.localPlugins){
				root = new File("./").toPath();
			}
			Path pluginDir = Files.createDirectories(Paths.get(root.resolve("plugins").toUri()));
			Files.walk(pluginDir).filter(plugin -> plugin.getFileName().toString().contains(".jar")).distinct()
					.forEach(plugin -> set.add(plugin));
			log.info("fileCount: {}", pluginDir.toFile().listFiles().length);
			return set;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public FXMLLoader getFXLoaderFor(PluginType type) {
		try {
			List<Plugin> configExtensions = plugins.stream().filter(plugin -> plugin.getClass().getAnnotation(PluginDescriptor.class).type() == type).collect(Collectors.toList());
			if (configExtensions.isEmpty()) {
				log.error("No plugin found for type {}.", type);
				throw new IllegalArgumentException("Unable to find plugin for type " + type);
			} else {
				log.info("Found plugins {}", configExtensions.stream().map(plugin -> plugin.getClass().getName()).collect(Collectors.joining(",")));
			}
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(configExtensions.get(0).getFxmlFiles().stream().filter(val -> val.toString().endsWith(configExtensions.get(0).getFXML())).findFirst().orElse(null));
			log.info("Loading FXML from {}", loader.getLocation());
			return loader;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public FXController getFXControllerFor(PluginType type) {
		try {
			List<Plugin> configExtensions = plugins.stream().filter(plugin -> plugin.getClass().getAnnotation(PluginDescriptor.class).type() == type).collect(Collectors.toList());
			if (configExtensions.isEmpty()) {
				log.error("No plugin found for type {}.", type);
				throw new IllegalArgumentException("Unable to find plugin for type " + type);
			}
			return configExtensions.get(0).getController().getClass().newInstance();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public ConfigExtensionBase getConfigForType(PluginType type) throws IllegalArgumentException {
		List<Plugin> configExtensions = plugins.stream().filter(plugin -> plugin.getClass().getAnnotation(PluginDescriptor.class).type().equals(type)).collect(Collectors.toList());
		if (configExtensions.isEmpty()) {
			log.error("No plugin found for type {}.", type);
			throw new IllegalArgumentException("Unable to find plugin for type " + type);
		}
		return configExtensions.get(0).getConfig();
	}
	
	public LoaderExtensionBase getLoaderForType(PluginType type) throws IllegalArgumentException {
		List<Plugin> loaderExtensions = plugins.stream().filter(loader -> loader.getClass().getAnnotation(PluginDescriptor.class).type().equals(type)).collect(Collectors.toList());
		if (loaderExtensions.isEmpty()) {
			log.error("No loader found for type {}.", type);
			throw new IllegalArgumentException("Unable to find loader for type " + type);
		}
		return loaderExtensions.get(0).getLoader();
	}

	public Plugin getPluginForType(PluginType type) throws IllegalArgumentException {
		List<Plugin> filtered = plugins.stream().filter(loader -> loader.getClass().getAnnotation(PluginDescriptor.class).type().equals(type)).collect(Collectors.toList());
		if (filtered.isEmpty()) {
			log.error("No plugin found for type {}.", type);
			throw new IllegalArgumentException("Unable to find plugin for type " + type);
		}
		return filtered.get(0);
	}
	
	public ObservableList<PluginType> getPluginTypes() {
		return FXCollections.observableArrayList(plugins.stream().map((value) -> value.getClass().getAnnotation(PluginDescriptor.class).type()).collect(Collectors.toList()));
	}

	public static PluginManager create() {
		return new PluginManager(Selection.progressListener, true);
	}
	
	public static PluginManager get() {
		if (Objects.isNull(instance))
			return create();
		return instance;
	}

	public PluginManager reload() {
		if (isLoading() || Objects.isNull(pluginClassLoader)) {
			throw new IllegalStateException("Unable to reload plugins.");
		}
		pluginClassLoader.clearAssertionStatus();
		plugins.clear();
		loadPlugins();
		return this;
	}

}
