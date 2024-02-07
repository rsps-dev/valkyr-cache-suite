package suite.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import misc.CustomTab;
import misc.RsMesh;
import store.CacheLibrary;
import store.cache.index.Index;
import store.cache.index.archive.Archive;
import store.codec.model.Mesh;
import store.io.impl.InputStream;
import store.io.impl.OutputStream;
import store.plugin.PluginManager;
import store.plugin.PluginType;
import store.plugin.extension.ConfigExtensionBase;
import store.plugin.extension.FXController;
import store.plugin.models.NamedValueObject;
import store.plugin.nodes.TupleCellFactory;
import store.utilities.ReflectionUtils;
import suite.Main;
import suite.dialogue.Dialogues;
import suite.dialogue.InformationDialogue;
import utility.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @author ReverendDread on 4/4/2020
 * https://www.rune-server.ee/members/reverenddread/
 * @project ValkyrCacheSuite
 */
public class ConfigEditor extends FXController {

    protected CustomTab tab;
    protected ConfigExtensionBase editing;
    protected ObservableList<ConfigExtensionBase> definitions = FXCollections.observableArrayList();

    protected UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("-?([1-9][0-9]*)?")) {
            return change;
        }
        return null;
    };

    @FXML protected ListView<ConfigExtensionBase> listView;
    @FXML protected TableView<NamedValueObject> table;
    @FXML protected TableColumn<NamedValueObject, String> name;
    @FXML protected TableColumn<NamedValueObject, Node> value;
    @FXML protected TextField search_bar;
    @FXML protected AnchorPane meshViewWrapper;
    @FXML protected ImageView meshView;
    @FXML protected ComboBox<RsMesh> meshes;

    @Override
    public void initialize(CustomTab tab, boolean refresh, int lastId) {
        this.tab = tab;
        setup();
    }

    protected void setup() {

        table.setEditable(true);
        value.setEditable(true);
        name.setEditable(false);

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            this.editing = newValue;
            this.tab.setText(getInfo().getType().toString() + " editor - " + editing.toString());
            table.getItems().clear();
            List<NamedValueObject> obj = ReflectionUtils.getValueAsNamedValueList(this.editing);
            name.setCellValueFactory(cell -> cell.getValue().getName());
            value.setCellValueFactory(cell -> cell.getValue().getDisplayNode());
            value.setCellFactory(cell -> new TupleCellFactory());
            table.getItems().addAll(obj);
            meshes.getItems().clear();
            if (this.editing.getMeshIds() != null) {
                List<Integer> meshIds = this.editing.getMeshIds();
                for (int model : meshIds) {
                    if (model == -1)
                        continue;
                    meshes.getItems().add(new RsMesh(model));
                }
                if (!meshes.getItems().isEmpty())
                    meshes.getSelectionModel().select(0);
            }
        });

        search_bar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("")) {
                listView.getItems().setAll(definitions);
                return;
            }
            List<ConfigExtensionBase> matching = definitions.stream().filter(config -> config.toString().toLowerCase().contains(newValue.toLowerCase())).collect(Collectors.toList());
            listView.getItems().setAll(matching);
        });

        ContextMenu context_menu = new ContextMenu();
        listView.setContextMenu(context_menu);

        MenuItem create = new MenuItem("Create");
        create.setOnAction((event) -> {
            try {
                int id = Dialogues.integerInput(null, "Enter an id...", PluginManager.get().getLoaderForType(getInfo().getType()).getDefinitions().size() + 1);
                ConfigExtensionBase blank_def = PluginManager.get().getConfigForType(getInfo().getType()).getClass().newInstance();
                blank_def.id = id;
                blank_def.copy(editing);
                blank_def.onCreate();
                this.editing = blank_def;
                if (!getInfo().is317()) {
                    save();
                }
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
                InformationDialogue.create("Error", "An error has occured.", Alert.AlertType.ERROR,
                        "Unable to create a new config.", "",
                        "Please notify ReverendDread about this error, and a way to reproduce it.");
            }
        });

        Menu importMenu = new Menu("Import");
        MenuItem importDat = new MenuItem(".dat");
        importDat.setOnAction(event -> {
            try {
                List<File> files = RetentionFileChooser.showOpenMultipleDialog("Select .dat files to import", Main.getSelection().stage, new FileChooser.ExtensionFilter("DAT Files", "*.dat"));
                Main.getSelection().createTask("Importing...", true, TaskUtil.create(() -> {
                    for (File file : files) {
                        byte[] data = Files.readAllBytes(file.toPath());
                        ConfigExtensionBase newItem = PluginManager.get().getConfigForType(getInfo().getType()).getClass().newInstance();
                        newItem.id = StringUtilities.stripId(file.getName());
                        InputStream buffer = new InputStream(data);
                        for (;;) {
                            int opcode = buffer.readUnsignedByte();
                            if (opcode == 0)
                                break;
                            newItem.decode(opcode, buffer);
                        }
                        CacheLibrary.get().getIndex(getInfo().getIndex()).getArchive(getInfo().getArchive()).addFile(newItem.id, data);
                        CacheLibrary.get().getIndex(getInfo().getIndex()).update(Selection.progressListener);
                        PluginManager.get().getLoaderForType(getInfo().getType()).reload();
                        Platform.runLater(() -> initialize(tab, false, newItem.id));
                    }
                    return true;
                }));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        importMenu.getItems().addAll(importDat);

        Menu exportMenu = new Menu("Export");
        MenuItem exportDat = new MenuItem(".dat");
        exportDat.setOnAction((event) -> {
            ConfigExtensionBase selectedItem = listView.getSelectionModel().getSelectedItem();
            File file = RetentionFileChooser.showSaveDialog("Save as...", Main.getPrimaryStage(), selectedItem.id + "", FilterMode.DAT);
            try {
                OutputStream data = selectedItem.encode(new OutputStream());
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
                dos.write(data.flip());
                dos.close();
            } catch (IOException e) {
                Dialogues.alert(Alert.AlertType.ERROR, "Error", "An error has occured.", StringUtilities.getStackTrace(e), true);
            }
        });
        exportMenu.getItems().addAll(exportDat);

        Menu replaceFrom = new Menu("Replace from");
        MenuItem replaceFromId = new MenuItem("ID");
        replaceFromId.setOnAction(event -> {
            ConfigExtensionBase selected = listView.getSelectionModel().getSelectedItem();
            if (Objects.nonNull(selected)) {
                int id = Dialogues.integerInput(null, "Enter an id...");
                Main.getSelection().createTask("Replacing config...", true, TaskUtil.create(() -> {
                    if (id > -1) {
                        ConfigExtensionBase replacing = definitions.get(id);
                        if (getInfo().is317()) {
                            selected.copy(replacing);
                            CacheLibrary.get().getIndex(getInfo().getIndex()).update(Selection.progressListener);
                        } else {
                            OutputStream buffer = replacing.encode(new OutputStream());
                            CacheLibrary.get().getIndex(getInfo().getIndex()).getArchive(getInfo().getArchive()).addFile(selected.id, buffer.flip());
                            CacheLibrary.get().getIndex(getInfo().getIndex()).update(Selection.progressListener);
                            PluginManager.get().getLoaderForType(getInfo().getType()).reload();
                        }
                        Platform.runLater(() -> initialize(tab, false, id));
                        return true;
                    }
                    return false;
                }));
            }
        });
        MenuItem replaceFromDat = new MenuItem(".dat");
        replaceFromDat.setOnAction(event -> {
            try {
                int id = listView.getSelectionModel().getSelectedItem().id;
                File file = RetentionFileChooser.showOpenDialog("Select a config to import.", Main.getSelection().stage, FilterMode.DAT);
                Main.getSelection().createTask("Replacing item...", true, TaskUtil.create(() -> {
                    byte[] data = Files.readAllBytes(file.toPath());
                    ConfigExtensionBase newObject = PluginManager.get().getConfigForType(getInfo().getType()).getClass().newInstance();
                    newObject.id = id;
                    InputStream buffer = new InputStream(data);
                    for (;;) {
                        int opcode = buffer.readUnsignedByte();
                        if (opcode == 0)
                            break;
                        newObject.decode(opcode, buffer);
                    }
                    CacheLibrary.get().getIndex(getInfo().getIndex()).getArchive(getInfo().getArchive()).addFile(id, data);
                    CacheLibrary.get().getIndex(getInfo().getIndex()).update(Selection.progressListener);
                    PluginManager.get().getLoaderForType(getInfo().getType()).reload();
                    Platform.runLater(() -> initialize(tab, false, newObject.id));
                    return true;
                }));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        replaceFrom.getItems().addAll(replaceFromId, replaceFromDat);

        meshView.fitWidthProperty().bind(meshViewWrapper.widthProperty());
        meshView.fitHeightProperty().bind(meshViewWrapper.heightProperty());

        definitions.setAll(PluginManager.get().getLoaderForType(getInfo().getType()).getDefinitions().values());
        listView.getItems().setAll(definitions);

        context_menu.getItems().setAll(create, importMenu, exportMenu, replaceFrom);
    }

    private void pack317Config() {
        final OutputStream[] streams = editing.encodeConfig317(getInfo().getFileName());

        if (streams != null) {
            CacheLibrary.get().getIndex(0).getArchive(2).addFile(getInfo().getFileName() + ".dat", streams[0].buffer);

            if (streams[1] != null) {
                CacheLibrary.get().getIndex(0).getArchive(2).addFile(getInfo().getFileName() + ".idx", streams[1].buffer);
            }
        }
    }


    @Override
    public void save() {
        try {
            if (Objects.nonNull(editing)) {
                Task<Boolean> saving = new Task<Boolean>() {

                    @Override
                    protected Boolean call() throws Exception {
                        try {
                            table.getItems().stream().forEach(item -> {
                                if (item.getDisplayNode() == null) {
                                    return;
                                }
                                for (Field field : editing.getClass().getFields()) {
                                    if (field.getName().equalsIgnoreCase(item.getName().get())) {
                                        ReflectionUtils.setValue(editing, field, item.getDisplayNode().get());
                                    }
                                }
                            });
                            if (getInfo().is317()) {
                                pack317Config();
                            } else {
                                OutputStream encoded = editing.encode(new OutputStream());
                                CacheLibrary.get().getIndex(getInfo().getIndex()).getArchive(getInfo().getArchive()).addFile(editing.id, encoded.flip());
                            }
                            CacheLibrary.get().getIndex(getInfo().getIndex()).update(Selection.progressListener);
                            PluginManager.get().getLoaderForType(getInfo().getType()).reload();
                            Platform.runLater(() -> initialize(tab, true, editing.id));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }

                };
                Main.getSelection().createTask("Saving config " + editing.id + "...", true, saving);
            }
        } catch (Exception e) {
            InformationDialogue.create("Error", "An error has occured.", Alert.AlertType.ERROR,
                    "Unable to save, please your check spelling.", "", StringUtilities.getStackTrace(e));
        }
    }

    @Override
    public void update() {
        try {
            if (Objects.nonNull(editing)) {
                Task<Boolean> saving = new Task<Boolean>() {

                    @Override
                    protected Boolean call() throws Exception {
                        try {
                            table.getItems().stream().forEach(item -> {
                                if (item.getDisplayNode() == null) {
                                    return;
                                }
                                for (Field field : editing.getClass().getFields()) {
                                    if (field.getName().equalsIgnoreCase(item.getName().get())) {
                                        ReflectionUtils.setValue(editing, field, item.getDisplayNode().get());
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    }

                };
                Main.getSelection().createTask("Updating config " + editing.id + "...", true, saving);
            }
        } catch (Exception e) {
            InformationDialogue.create("Error", "An error has occured.", Alert.AlertType.ERROR,
                    "Unable to update config, please your check spelling.", "", StringUtilities.getStackTrace(e));
        }
    }

    /**
     * Acts as a default item config editor.
     * @return
     */
    @Override
    public ConfigEditorInfo getInfo() {
        return ConfigEditorInfo.builder().index(2).archive(10).type(PluginType.ITEM).build();
    }

    public byte[] getConfigFile317(String name) {
        CacheLibrary cache = CacheLibrary.get();
        Index index = cache.getIndex(0);
        Archive archive = index.getArchive(2);
        store.cache.index.archive.file.File file = archive.getFile(name);
        return file.getData();
    }

}
