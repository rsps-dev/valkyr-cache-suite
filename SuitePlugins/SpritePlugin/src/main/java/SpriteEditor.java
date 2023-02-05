

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import misc.CustomTab;
import store.CacheLibrary;
import store.cache.index.archive.Archive;
import store.io.impl.OutputStream;
import store.plugin.PluginManager;
import store.plugin.PluginType;
import store.plugin.extension.ConfigExtensionBase;
import store.plugin.extension.FXController;
import store.plugin.extension.LoaderExtensionBase;
import suite.Main;
import suite.controller.Selection;
import suite.dialogue.Dialogues;
import suite.dialogue.InformationDialogue;
import utility.ConfigEditorInfo;
import utility.StringUtilities;
import utility.TaskUtil;

/**
 * 
 * @author ReverendDread Jul 10, 2018
 */
public class SpriteEditor extends FXController {

	private CustomTab tab;
	private ConfigExtensionBase selected;
	private int frame;
	
	@FXML private ListView<ConfigExtensionBase> archives;
	@FXML private ListView<SpriteFrame> frames;
	@FXML private Canvas canvas;
	@FXML private TextField search_bar;

	@FXML
	private void select_frame() {
		
		SpriteLoader.cleanup();
		
		SpriteFrame sprite_frame = frames.getSelectionModel().getSelectedItem();
		if (Objects.isNull(sprite_frame))
			return;
		Image image = sprite_frame.getImage();
		frame = frames.getSelectionModel().getSelectedIndex();
		if (image != null) {
			tab.setText("Sprites Editor - Viewing Sprite " + selected.id + ", " + frame);
			draw(image);
		} else {
			InformationDialogue.create("Error", "Unable to load sprite frame, no image exists.", AlertType.ERROR, "",
					"", null);
		}
	}

	@FXML
	private void select_archive() {

		SpriteLoader.cleanup();
		selected = archives.getSelectionModel().getSelectedItem();
		SpriteContainer container = (SpriteContainer) PluginManager.get().getLoaderForType(PluginType.SPRITE).getDefinitions().get(selected.id);
		
		try {
			frames.getItems().clear();
			tab.setText("Sprites Editor - Viewing Sprite " + selected.id + ", 0");
			if (container.hasImages(8, selected.id, 0)) {
				List<SpriteFrame> sprites = container.requestLoad().toSpriteFrames();
				if (sprites.get(0) == null) {
					draw(null);
					return;
				}
				frames.getItems().setAll(sprites);
				draw(frames.getItems().get(0).getImage());
			}
		} catch (Exception e) {
			InformationDialogue.create("Error", "Unable to load sprite container.", AlertType.ERROR,
					"Sprite container has failed to decode.", "", StringUtilities.getStackTrace(e));
			e.printStackTrace();
		}

	}

	/**
	 * Draws an image on the canvas.
	 * 
	 * @param image
	 */
	private void draw(Image image) {
		final GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
		gc.drawImage(image, 10, 10);
	}

	/**
	 * Adds context menus to the lists.
	 */
	private void addContextMenus() {
		
		/* The archive ListView context menu. */
		ContextMenu archiveContext = new ContextMenu();

		/* The create menu item for creating new sprite collections. */
		MenuItem create = new MenuItem("Create container");

		/* Create function for create option */
		create.setOnAction((action) -> {
			try {
				/* Default image added to sprite collection upon creating one.*/
				BufferedImage placeholder = ImageIO.read(Main.class.getClassLoader().getResource("hijabnigga.jpeg"));
				SpriteContainer sprite = (SpriteContainer) PluginManager.get().getConfigForType(PluginType.SPRITE).getClass().newInstance();
				int id = Dialogues.integerInput("Enter an id.", "Enter an id, otherwise the next in order will be created.", findNextAvailable());
				if (id != -1) {
					sprite.setId(id);
					sprite.addImage(placeholder);
					Task<Boolean> task = TaskUtil.create(() -> {
						byte[] data = sprite.encode(new OutputStream()).flip();
						CacheLibrary.get().getIndex(getInfo().getIndex()).addArchive(id).addFile(0, data);
						CacheLibrary.get().getIndex(getInfo().getIndex()).update(Selection.progressListener);
						PluginManager.get().getLoaderForType(PluginType.SPRITE).reload();
						Platform.runLater(() -> initialize(tab, true, id));
						Dialogues.alert(AlertType.INFORMATION, "Information", "Created sprite container " + id, null, false);
						return true;
					});
					Main.getSelection().createTask("Creating sprite container...", true, task);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		/* Add create option to context menu */
		archiveContext.getItems().add(create);

		/* The dump frames menu item for dumping sprite collections frames. */
		MenuItem dumpFrames = new MenuItem("Dump Frames");

		/* Create function for dump frames option */
		dumpFrames.setOnAction((action) -> {
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setTitle("Choose your dump directory.");
			File directory = chooser.showDialog(Main.getPrimaryStage());
			if (directory == null)
				return;
			SpriteContainer container = (SpriteContainer) selected;
			if (container.hasImages(8, selected.id, 0)) {
				List<BufferedImage> images = container.requestLoad().getBufferedImages();
				for (int index = 0; index < images.size(); index++) {
					try {
						File output = new File(directory + File.separator + selected.id + "-" + index + ".png");
						ImageIO.write(images.get(index), "png", output);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		/* Add dump frames option to context menu */
		archiveContext.getItems().add(dumpFrames);

		/* Adds the context menu to archives ListView. */
		archives.setContextMenu(archiveContext);

		/* The frames ListView context menu */
		ContextMenu frameContext = new ContextMenu();

		/* The add frame menu item for adding new frames to a sprite collection. */
		MenuItem add = new MenuItem("Add Frame");

		add.setOnAction((action) -> {
			try {
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Choose sprite to pack.");
				chooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg"));
				File file = chooser.showOpenDialog(Main.getPrimaryStage());
				if (Objects.nonNull(file)) {
					SpriteContainer container = (SpriteContainer) PluginManager.get().getLoaderForType(PluginType.SPRITE).getDefinitions().get(selected.id); //this is just a copy not a reference
					container.requestLoad().addImage(ImageIO.read(file));
					frames.getItems().setAll(container.toSpriteFrames());
					CacheLibrary.get().getIndex(getInfo().getIndex()).addArchive(container.id).addFile(0, container.encode(new OutputStream()).flip());
				}
			} catch (IOException e) {
				Dialogues.alert(AlertType.ERROR, "Error", "Palette size is to large!", null, false);
				e.printStackTrace();
			}
		});
		frameContext.getItems().add(add);

		MenuItem replace = new MenuItem("Replace Frame");
		replace.setOnAction((action) -> {
			try {
				FileChooser chooser = new FileChooser();
				chooser.setTitle("Choose sprite to pack.");
				chooser.getExtensionFilters()
						.addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg"));
				File file = chooser.showOpenDialog(Main.getPrimaryStage());
				if (file != null) {
					BufferedImage image = ImageIO.read(file);
					SpriteContainer container = (SpriteContainer) PluginManager.get().getLoaderForType(PluginType.SPRITE).getDefinitions().get(selected.id);
					container.requestLoad().replaceImage(image, frame);
					if (image.getHeight() > container.getBiggestHeight()) {
						container.setBiggestHeight(image.getHeight());
					}
					if (image.getWidth() > container.getBiggestWidth()) {
						container.setBiggestWidth(image.getWidth());
					}
					frames.getItems().setAll(container.toSpriteFrames());
					CacheLibrary.get().getIndex(8).addArchive(selected.id).addFile(0, container.encode(new OutputStream()).flip());
				}
				select_archive();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		frameContext.getItems().add(replace);

		MenuItem delete = new MenuItem("Delete Frame");
		delete.setOnAction((action) -> {
			SpriteContainer container = (SpriteContainer) PluginManager.get().getLoaderForType(PluginType.SPRITE).getDefinitions().get(selected.id);
			container.removeImage(frame);
			CacheLibrary.get().getIndex(8).addArchive(container.id).addFile(0, container.encode(new OutputStream()).flip());
			frames.getItems().setAll(container.toSpriteFrames());
		});
		frameContext.getItems().add(delete);
		frames.setContextMenu(frameContext);

		search_bar.textProperty().addListener((observable, oldValue, newValue) -> {
			int id = Integer.parseInt(newValue);
			selected = PluginManager.get().getLoaderForType(PluginType.SPRITE).getDefinitions().get(id);
			archives.getSelectionModel().select(id);
			archives.scrollTo(id);
			select_archive();
		});

		archives.addEventFilter(KeyEvent.ANY, event -> {
			selected = archives.getSelectionModel().getSelectedItem();
			select_archive();
		});

		frames.addEventFilter(KeyEvent.ANY, event -> {
			frame = frames.getSelectionModel().getSelectedIndex();
			select_frame();
		});
	}

	private int findNextAvailable() {
		int lastId = CacheLibrary.get().getIndex(getInfo().getIndex()).getLastArchive().getId();
		for (int i = 0; i < lastId; i++) {
			Archive archive = CacheLibrary.get().getIndex(getInfo().getIndex()).getArchive(i);
			if (archive == null) {
				return i;
			}
		}
		return lastId + 1;
	}

	@FXML
	private void dumpAllSprites() {
		boolean confirmed = JOptionPane.showConfirmDialog(null,
				"Are you sure you want to dump all sprite frames? This may take awhile, but will happen in the background.",
				"Confirmation", JOptionPane.YES_NO_OPTION) == 0;
		if (confirmed) {
			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setTitle("Select your dumping location.");
			File directory = chooser.showDialog(Main.getPrimaryStage());
			if (directory != null) {
				Main.getSelection().createTask("Dumping Sprites...", true, new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						try {
							LoaderExtensionBase loader = PluginManager.get().getLoaderForType(PluginType.SPRITE);
							int size = loader.getDefinitions().size() + 1;
							for (int index = 0; index < size; index++) {
								SpriteContainer container = (SpriteContainer) loader.getDefinitions().get(index);
								if (container == null)
									continue;
								container.requestLoad();
								if (container.hasImages(8, index, 0)) {
									for (int image = 0; image < container.getImages().size(); image++) {
										if (container.getBufferedImages().get(image) == null)
											continue;
										File file = new File(directory + File.separator + index + "-" + image + ".png");
										ImageIO.write(container.getBufferedImages().get(image), "png", file);
									}
								}
							}
							System.gc();
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}

				});
			}
		}
	}

	@Override
	public void initialize(CustomTab tab, boolean refresh, int lastId) {
		this.tab = tab;
		this.archives.getItems().setAll(PluginManager.get().getLoaderForType(getInfo().getType()).getDefinitions().values());
		SpriteContainer container = (SpriteContainer) PluginManager.get().getLoaderForType(getInfo().getType()).getDefinitions().get(lastId);
		this.frames.getItems().setAll(container.requestLoad().toSpriteFrames());
		addContextMenus();
	}

	@Override
	public void save() {
		Main.getSelection().createTask("Saving sprites...", true, TaskUtil.create(() -> CacheLibrary.get().getIndex(getInfo().getIndex()).update()));
	}

	@Override
	public ConfigEditorInfo getInfo() {
		return ConfigEditorInfo.builder().index(8).type(PluginType.SPRITE).build();
	}

}