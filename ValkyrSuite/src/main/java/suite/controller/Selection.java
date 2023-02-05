package suite.controller;

import java.io.File;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import misc.CustomTab;
import persistable.Persistable;
import store.CacheLibrary;
import store.CacheLibraryMode;
import suite.annotation.PluginDescriptor;
import store.plugin.PluginManager;
import store.plugin.PluginType;
import store.plugin.extension.FXController;
import store.progress.AbstractProgressListener;
import suite.Constants;
import suite.Main;
import suite.dialogue.Dialogues;
import suite.opengl.RendererRepository;
import utility.PluginMenuItem;
import utility.StringUtilities;

@Slf4j
public class Selection {

	public static boolean debug;
	public static boolean loaded_cache;
	public static CacheLibrary cache;
	public Stage stage;
	public static AbstractProgressListener progressListener;
	public static boolean terminate;

	@FXML @Getter
	public TabPane tabs;
	@FXML
	private ComboBox<PluginMenuItem> toolChoice = new ComboBox<>();
	@FXML
	private ProgressBar progress_bar;
	@FXML
	private Button open;
	@FXML
	private Text progress_text;

	public void initialize(Stage stage) {
		try {
			this.stage = stage;
			progressListener = new AbstractProgressListener() {

				@Override
				public void change(double progress, String message) {
					Platform.runLater(() -> {
						progress_text.setText(message);
						progress_bar.setProgress(progress);
						//Find alternative way of doing this, current causes issues because of javafx bug.
//						Timeline timeline = new Timeline();
//						KeyValue keyValue = new KeyValue(progress_bar.progressProperty(), progress);
//						KeyFrame keyFrame = new KeyFrame(new Duration(200), keyValue);
//						timeline.getKeyFrames().add(keyFrame);
//						timeline.play();
					});
				}

				@Override
				public void finish(String title, String message) {
					fadeProgress();
					System.out.println("Finish loading");
				}

			};
			Persistable persistable = Constants.settings;
			if (persistable != null && (persistable.cacheDir != null) && !persistable.cacheDir.isEmpty()) {
				File dir = new File(persistable.cacheDir);
				if (dir.exists()) {
					performStartup();
				} else
					open_cache();
			} else
				open_cache();
			
			stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
				if (event.isControlDown() && event.getCode() == KeyCode.S) {
					save();
				}
				if (event.isControlDown() && event.getCode() == KeyCode.D) {
					update();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			Platform.exit();
		}
	}

	public void performStartup() {
		createTask("Performing startup...", true, new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				CacheLibrary.create(Constants.settings.cacheDir, CacheLibraryMode.UN_CACHED, null);
				PluginManager.create();
				toolChoice.getItems().setAll(PluginManager.get().getPluginTypes().stream().map(PluginMenuItem::create).collect(Collectors.toList()));
				toolChoice.setCellFactory(param -> new ListCell<PluginMenuItem>() {

					@Override
					public void updateItem(PluginMenuItem item, boolean empty) {
						super.updateItem(item, empty);
						if (Objects.nonNull(item)) {
							setText(item.getText());
							Tooltip tt = new Tooltip();
							PluginDescriptor descriptor = PluginManager.get().getPluginForType(item.getType()).getDescriptor();
							if (Objects.nonNull(descriptor)) {
								String tip = descriptor.description() + "\n"
										+ "Revision: " + descriptor.version() + "\n"
										+ "Author: " + descriptor.author();
								tt.setGraphic(new Label(tip));
								tt.fontProperty().set(new Font(14));
							} else {
								log.info("Tooltip for plugin {} is null", item.getText());
							}
							setTooltip(tt);
						} else {
							setText(null);
							setTooltip(null);
						}
					}

				});
				return null;
			}

		});
	}

	@FXML
	public void openTool() {
		PluginMenuItem selected = toolChoice.getSelectionModel().getSelectedItem();
		if (Objects.isNull(CacheLibrary.get())) {
			Dialogues.alert(AlertType.ERROR, "An error has occured.", null,
					"Please load a cache before attempting to open an editor.", true);
			return;
		}	
		openEditor(selected.getType());
	}
	
	public void openEditor(PluginType type) {
		try {
			FXMLLoader loader = PluginManager.get().getFXLoaderFor(type);
			FXController controller = PluginManager.get().getFXControllerFor(type);
			loader.setController(controller);
			Node node = loader.load();
			CustomTab tab = new CustomTab(controller, type);
			controller.initialize(tab, true, 0);
			tab.setText(StringUtilities.getFormattedEnumName(type.name()) + " editor");
			tab.setContent(node);
			tab.setOnClosed((e) -> RendererRepository.get().terminateWrapper(tab.getUuid()));
			//Probably doesnt need both of these.
			//tab.setOnCloseRequest((e) -> RendererRepository.get().terminateWrapper(tab.getUuid()));
			tabs.getTabs().add(tab);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves content from current tab.
	 */
	@FXML
	public void save() {
		if (tabs.getTabs().isEmpty()) {
			Dialogues.alert(AlertType.ERROR, "An error has occured.", null, "Please select an editor before attempting to save.", true);
			return;
		}
		CustomTab tab = (CustomTab) tabs.getSelectionModel().getSelectedItem();
		tab.getController().save();
	}

	public void update() {
		if (tabs.getTabs().isEmpty()) {
			Dialogues.alert(AlertType.ERROR, "An error has occured.", null, "Please select an editor before attempting to save.", true);
			return;
		}
		CustomTab tab = (CustomTab) tabs.getSelectionModel().getSelectedItem();
		tab.getController().update();
	}

	/**
	 * Opens settings window.
	 */
	@FXML
	public void open_settings() {
		Parent root;
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("/Settings.fxml"));
			root = loader.load();
			Stage stage = new Stage();
			stage.getIcons().add(new Image(Constants.FAVICON));
			stage.setTitle(Constants.WINDOW_NAME + " - Settings");
			stage.setScene(new Scene(root, 600, 150));
			stage.setResizable(false);
			Settings settings = (Settings) loader.getController();
			settings.startup();
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the cache from a directory.
	 */
	@FXML
	public void open_cache() {
		tabs.getTabs().clear();
		DirectoryChooser dir_chooser = new DirectoryChooser();
		dir_chooser.setTitle("Locate Cache Directory..");
		Stage stage = Main.getPrimaryStage();
		File default_dir = new File(System.getProperty("user.home") + "\\Desktop\\");
		dir_chooser.setInitialDirectory(default_dir);
		File directory = dir_chooser.showDialog(stage);
		if (directory != null) {
			Constants.settings.cacheDir = directory.toString() + "\\";
			Constants.settings.save();
			performStartup();
			return;
		}
		Dialogues.alert(AlertType.ERROR, "Error", null, "Failed to locate cache directory.", true);
	}

	/**
	 * Safely exits the program.
	 */
	@FXML
	private void quit() {
		System.exit(0);
	}

	/**
	 * Creates a new task on a new thread.
	 * 
	 * @param task the task to start.
	 */
	public void createTask(String task_title, boolean locks, Task<?> task) {

		//stage.setTitle(Constants.WINDOW_NAME + " - " + task_title);

		progress_text.setText(task_title);
		progress_bar.setProgress(0);
		progress_text.setOpacity(1);
		progress_bar.setOpacity(1);
		progress_bar.setVisible(true);
		toolChoice.setDisable(true);
		open.setDisable(true);

		new Thread(task).start();

		task.setOnSucceeded(e -> {
			open.setDisable(false);
			progress_bar.setProgress(0);
			toolChoice.setDisable(false);
			progress_text.setOpacity(0);
		});

		task.setOnFailed(e -> {
			progress_bar.setProgress(0);
			open.setDisable(false);
			toolChoice.setDisable(false);
			progress_text.setOpacity(0);
		});
	}

	private void fadeProgress() {
		Task finish = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				FadeTransition progressTransition = new FadeTransition(Duration.millis(500), progress_bar);
				progressTransition.setFromValue(1.0);
				progressTransition.setToValue(0);
				progressTransition.play();
				FadeTransition textTransition = new FadeTransition(Duration.millis(500), progress_text);
				textTransition.setFromValue(1.0);
				textTransition.setToValue(0);
				textTransition.play();
				return null;
			}
		};
		new Thread(finish).start();
	}

	public AbstractProgressListener getProgressListener() {
		return new AbstractProgressListener() {

			@Override
			public void change(double progress, String message) {
				progress_bar.setVisible(true);
				progress_bar.setProgress(progress / 100);
				progress_text.setText(message);
				log.info("[{}] - {}", progress, message);
				if (progress >= 100) {
					progress_bar.setVisible(false);
				}
			}

			@Override
			public void finish(String title, String message) {
				progress_bar.setVisible(false);
			}

		};
	}

}
