/**
 * 
 */
package store.plugin.models;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.HBox;
import javafx.util.converter.IntegerStringConverter;
import lombok.Getter;
import store.CacheLibrary;
import store.cache.index.archive.Archive;
import suite.Main;
import utility.RetentionFileChooser;

/**
 * @author ReverendDread Sep 17, 2019
 */
@Getter
public class NamedValueObject {

	private StringProperty name;
	private ObjectProperty<Node> displayNode;
	private Field field;

	public NamedValueObject(String name, Field field, Object value) {
		this.field = field;
		this.name = new SimpleStringProperty(name);
		this.setDisplayNode(field, value);
	}

	/**
	 * @param
	 */
	private void setDisplayNode(Field field, Object item) {
		UnaryOperator<Change> integerFilter = change -> {
		    String newText = change.getControlNewText();
		    if (newText.matches("-?([1-9][0-9]*)?")) { 
		        return change;
		    }
		    return null;
		};

		Node graphic = null;
		//if (item != null) {
			if (field.getType().isAssignableFrom(String.class)) {
				graphic = (new TextField((String) item));
			} else if (field.getType().isAssignableFrom(int.class)) {
				graphic = (new TextField(Integer.toString((Integer) item)));
			} else if (field.getType().isAssignableFrom(short.class)) {
				graphic = (new TextField(Short.toString((Short) item)));
			} else if (field.getType().isAssignableFrom(char.class)) {
				graphic = (new TextField(Character.toString((char) item)));
			} else if (field.getType().isAssignableFrom(byte.class)) {
				byte value = (Byte) item;
				Slider slider = new Slider(Byte.MIN_VALUE, Byte.MAX_VALUE, value);
				graphic = (slider);
			} else if (field.getType().isAssignableFrom(long.class)) {
				graphic = (new TextField(Long.toString((Long) item)));
			} else if (field.getType().isAssignableFrom(boolean.class)) {
				CheckBox checkBox = new CheckBox();
				checkBox.setSelected((boolean) item);
				graphic = (checkBox);
			} else if (field.getType().isAssignableFrom(String[].class)) {

				HBox hbox = new HBox();
				hbox.getProperties().put("type", "strings");
				hbox.setSpacing(5);

				if (item == null) {
					item = new String[0];
				}

				ObservableList<String> items = FXCollections.observableArrayList((String[]) item);
				ComboBox<String> comboBox = new ComboBox<>(items);
				comboBox.setEditable(false);
				comboBox.getSelectionModel().select(0);
				comboBox.setValue(items.isEmpty() ? "" : items.get(0));

				// Create the "Edit" button
				Button edit = new Button("Edit");
				edit.setOnAction(event -> {
					ArrayEditorDialog.showArrayEditor(comboBox);
				});

				hbox.getChildren().addAll(comboBox, edit);
				graphic = hbox;

			} else if (field.getType().isAssignableFrom(int[].class)) {
				HBox hbox = new HBox();
				hbox.getProperties().put("type", "ints");

				hbox.setSpacing(5);
				if (item == null) {
					item = new int[0];
				}
				ObservableList<String> items = FXCollections.observableArrayList(Ints.asList((int[]) item).stream().map(String::valueOf).collect(Collectors.toList()));
				ComboBox<String> comboBox = new ComboBox<String>(items);
				comboBox.getEditor().setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
				comboBox.setEditable(true);
				comboBox.getSelectionModel().select(0);
				comboBox.setValue(items.isEmpty() ? "" : items.get(0));

				Button add = new Button("Add");
				add.setOnAction((event) -> {
					String text = comboBox.getEditor().getText();
					comboBox.getItems().add(text.isEmpty() ? "0" : text);
					comboBox.getSelectionModel().selectLast();
				});
				
				Button remove = new Button("Remove");
				remove.setOnAction((event) -> {
					int index = comboBox.getSelectionModel().getSelectedIndex();
					if (index != -1)
						comboBox.getItems().remove(index);
				});
				
				Button set = new Button("Set");
				set.setOnAction((event) -> {
					String newValue = comboBox.getEditor().getText();
					System.out.println("index: " + comboBox.getSelectionModel().getSelectedIndex());
					System.out.println("new value: " + newValue);
					comboBox.getItems().set(comboBox.getSelectionModel().getSelectedIndex(), newValue);
				});

				hbox.getChildren().setAll(comboBox, add, remove, set);

				if (field.getName().equalsIgnoreCase("models")) {
					Button dump = new Button("Dump models");
					dump.setOnAction((event) -> {
						File directory = RetentionFileChooser.showOpenFolderDialog(
								Main.getPrimaryStage().getScene().getWindow(),
								new File(System.getProperty("user.home")));
						comboBox.getItems().forEach((value) -> {
							if (!value.isEmpty()) {
								int id = Integer.parseInt(value);
								Archive archive = CacheLibrary.get().getIndex(7).getArchive(id);
								if (Objects.nonNull(archive)) {
									byte[] data = archive.getData();

									File file = new File(directory, id + ".dat");
									try {
										Files.write(file.toPath(), data);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						});
					});
					hbox.getChildren().add(dump);
				}

				graphic = (hbox);
			} else if (field.getType().isAssignableFrom(short[].class)) {
				HBox hbox = new HBox();
				hbox.getProperties().put("type", "shorts");
				hbox.setSpacing(5);
				if (item == null) {
					item = new short[0];
				}
				ObservableList<String> items = FXCollections.observableArrayList(Shorts.asList((short[]) item).stream().map(String::valueOf).collect(Collectors.toList()));
				ComboBox<String> comboBox = new ComboBox<>(items);
				comboBox.getEditor().setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
				comboBox.setEditable(true);
				comboBox.getSelectionModel().select(0);
				comboBox.setValue(items.isEmpty() ? "" : items.get(0));
				Button add = new Button("Add");
				add.setOnAction((event) -> {
					String text = comboBox.getEditor().getText();
					comboBox.getItems().add(text.isEmpty() ? "null" : text);
					comboBox.getSelectionModel().selectLast();
				});
				Button remove = new Button("Remove");
				remove.setOnAction((event) -> {
					int index = comboBox.getSelectionModel().getSelectedIndex();
					if (index != -1)
						comboBox.getItems().remove(index);
				});
				Button set = new Button("Set");
				set.setOnAction((event) -> {
					String newValue = comboBox.getEditor().getText();
					comboBox.getItems().set(comboBox.getSelectionModel().getSelectedIndex(), newValue);
				});
				hbox.getChildren().setAll(comboBox, add, remove, set);
				graphic = (hbox);
			} else if (field.getType().isAssignableFrom(HashMap.class)) {
				HBox hbox = new HBox();
				hbox.getProperties().put("type", "params");
				hbox.setSpacing(5);
				if (item == null) {
					item = new HashMap<Integer, Object>();
				}
				Map<String, String> params = ((Map<Integer, Object>) item).entrySet().stream().collect(Collectors.toConcurrentMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue())));
				ComboBox<String> keys = new ComboBox<String>(FXCollections.observableArrayList(params.keySet()));
				ComboBox<String> values = new ComboBox<String>(FXCollections.observableArrayList(params.values()));
				
				keys.getEditor().setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
				
				keys.setEditable(true);
				values.setEditable(true);
				
				keys.getSelectionModel().selectFirst();
				values.getSelectionModel().selectFirst();
				
				keys.valueProperty().addListener((obs, old, newVal) -> values.getSelectionModel().select(keys.getItems().indexOf(newVal)));
				values.valueProperty().addListener((obs, old, newVal) -> keys.getSelectionModel().select(values.getItems().indexOf(newVal)));
				
				Button add = new Button("Add Nig");
				add.setOnAction((event) -> {
					String text = keys.getEditor().getText();
					keys.getItems().add(text.isEmpty() ? "0" : text);
					keys.getSelectionModel().selectLast();
					text = values.getEditor().getText();
					values.getItems().add(text.isEmpty() ? "null" : text);
					values.getSelectionModel().selectLast();
				});
				
				Button remove = new Button("Remove");
				remove.setOnAction((event) -> {
					int index = keys.getSelectionModel().getSelectedIndex();
					if (index != -1) {
						keys.getItems().remove(index);
						values.getItems().remove(index);
					}
				});
				
				Button set = new Button("Set");
				set.setOnAction(event -> {
					String newValue = keys.getEditor().getText();
					keys.getItems().set(keys.getSelectionModel().getSelectedIndex(), newValue);
					newValue = values.getEditor().getText();
					values.getItems().set(values.getSelectionModel().getSelectedIndex(), newValue);
				});
				
				hbox.getChildren().setAll(keys, values, add, remove, set);
				graphic = (hbox);
			} else {
				graphic = (null);
			}
			displayNode = new SimpleObjectProperty<>(graphic);
		//}
	}

}
