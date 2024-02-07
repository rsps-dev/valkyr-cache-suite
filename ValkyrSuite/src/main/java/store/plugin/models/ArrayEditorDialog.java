package store.plugin.models;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayEditorDialog {
	public static void showArrayEditor(ComboBox<String> comboBox) {
		// Create a stage for the dialog
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Array Editor");

		// Create a TextArea to display/edit the items
		TextArea textArea = new TextArea(String.join("\n", comboBox.getItems()));
		textArea.setStyle("-fx-text-fill: white; -fx-control-inner-background: #1b1d23;");

		// Create buttons
		Button applyButton = new Button("Apply");
		applyButton.setStyle("-fx-background-color: #1b1d23; -fx-text-fill: white; -fx-background-radius: 0;");
		applyButton.setOnMouseEntered(e -> applyButton.setStyle("-fx-background-color: #323841; -fx-text-fill: white; -fx-background-radius: 0;"));
		applyButton.setOnMouseExited(e -> applyButton.setStyle("-fx-background-color: #1b1d23; -fx-text-fill: white; -fx-background-radius: 0;"));
		applyButton.setPadding(new Insets(10));
		applyButton.setOnAction(event -> {
			List<String> editedItems = Arrays.asList(textArea.getText().split("\\n"));
			comboBox.getItems().setAll(editedItems.stream().map(s -> s.isEmpty() || s.equalsIgnoreCase("null") ? null : s).collect(Collectors.toList()));
			stage.close();
		});

		Button cancelButton = new Button("Cancel");
		cancelButton.setStyle("-fx-background-color: #1b1d23; -fx-text-fill: white; -fx-background-radius: 0;");
		cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #323841; -fx-text-fill: white; -fx-background-radius: 0;"));
		cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: #1b1d23; -fx-text-fill: white; -fx-background-radius: 0;"));
		cancelButton.setPadding(new Insets(10));
		cancelButton.setOnAction(event -> stage.close());

		// Create an HBox for the buttons
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		buttonBox.getChildren().addAll(applyButton, cancelButton);

		// Create a layout for the dialog
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(10));
		layout.setBackground(new Background(new BackgroundFill(Color.web("#21252b"), CornerRadii.EMPTY, Insets.EMPTY)));
		layout.getChildren().addAll(textArea, buttonBox);

		// Set the scene
		Scene scene = new Scene(layout);
		scene.setFill(Color.web("#21252b"));
		stage.setScene(scene);

		// Show the stage
		stage.showAndWait();
	}
}
