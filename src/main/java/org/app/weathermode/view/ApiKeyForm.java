package org.app.weathermode.view;

import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Utility dialog to prompt the user for a Google API key.
 */
public class ApiKeyForm {
    /**
     * Shows a modal dialog asking for the API key.
     * @return An Optional containing the trimmed key if entered and non-empty, otherwise an empty Optional.
     */
    public static Optional<String> showAndWait() {
        // Prepare a small array to capture the user input
        final String[] inputHolder = new String[1];

        // Create the modal stage
        final Stage dialog = new Stage();
        dialog.setTitle("Inserisci Api Key Google");
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Build UI controls
        final Label prompt = new Label("API Key Google:");
        final TextField textField = new TextField();
        textField.setPromptText("Inserisci la tua API key");

        final Button okButton = new Button("Conferma");
        okButton.setDefaultButton(true);
        okButton.setOnAction(e -> {
            final String entered = textField.getText().trim();
            if (!entered.isEmpty()) {
                inputHolder[0] = entered;
            }
            dialog.close();
        });

        // Layout setup
        final VBox root = new VBox(10, prompt, textField, okButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(15));
        dialog.setScene(new Scene(root, 350, 125));
        dialog.setResizable(false);
        dialog.showAndWait();
        return Optional.ofNullable(inputHolder[0]);
    }

}
