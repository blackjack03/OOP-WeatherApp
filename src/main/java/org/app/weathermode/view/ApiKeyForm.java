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
public final class ApiKeyForm {

    private static final double DIALOG_WIDTH = 350;
    private static final double DIALOG_HEIGHT = 125;
    private static final double PADDING = 15;
    private static final double SPACING = 10;

    private static final String WINDOW_TITLE = "Inserisci Api Key Google";
    private static final String LABEL_TEXT = "API Key Google:";
    private static final String TEXT_PROMPT = "Inserisci la tua API key";
    private static final String BUTTON_TEXT = "Conferma";

    private ApiKeyForm() { }

    /**
     * Shows a modal dialog asking for the API key.
     * @return An Optional containing the trimmed key if entered and non-empty, otherwise an empty Optional.
     */
    public static Optional<String> showAndWait() {
        // Prepare a small array to capture the user input
        final String[] inputHolder = new String[1];

        // Create the modal stage
        final Stage dialog = new Stage();
        dialog.setTitle(WINDOW_TITLE);
        dialog.initModality(Modality.APPLICATION_MODAL);

        // Build UI controls
        final Label prompt = new Label(LABEL_TEXT);
        final TextField textField = new TextField();
        textField.setPromptText(TEXT_PROMPT);

        final Button okButton = new Button(BUTTON_TEXT);
        okButton.setDefaultButton(true);
        okButton.setOnAction(e -> {
            inputHolder[0] = textField.getText().trim();
            dialog.close();
        });

        // Layout setup
        final VBox root = new VBox(SPACING, prompt, textField, okButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(PADDING));
        dialog.setScene(new Scene(root, DIALOG_WIDTH, DIALOG_HEIGHT));
        dialog.setResizable(false);
        dialog.showAndWait();
        return Optional.ofNullable(inputHolder[0]);
    }

}
