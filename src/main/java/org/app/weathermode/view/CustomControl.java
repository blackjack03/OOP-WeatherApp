package org.app.weathermode.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.List;

import org.app.weathermode.model.Pair;

public class CustomControl extends VBox {

    @FXML
    private TextField input_text1;

    @FXML
    private ScrollPane scroll_1;

    @FXML
    private VBox buttonContainer;

    public CustomControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("city_selector.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    public void initialize() {
        input_text1.setOnKeyReleased(event -> {
            String text = input_text1.getText();
            List<Pair<String, String>> pairs = processText(text);

            // Rimuovi tutti i pulsanti esistenti
            buttonContainer.getChildren().clear();

            // Aggiungi i nuovi pulsanti basati sulla lista di Pair
            for (Pair<String, String> pair : pairs) {
                Button button = new Button(pair.getX());
                button.setOnAction(e -> handleButtonClick(pair.getY()));
                buttonContainer.getChildren().add(button);
            }
        });
    }

    // Funzione che prende il testo e restituisce una lista di Pair
    private List<Pair<String, String>> processText(String text) {
        // Logica di esempio, puoi personalizzarla
        return List.of(
            new Pair<>("Pulsante " + text + " - 1", "Valore 1"),
            new Pair<>("Pulsante " + text + " - 2", "Valore 2")
        );
    }

    // Funzione chiamata quando un pulsante viene cliccato
    private void handleButtonClick(String value) {
        // Logica per gestire il click
        System.out.println("Pulsante cliccato con valore: " + value);
    }

    public static class TestApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            CustomControl customControl = new CustomControl();
            Scene scene = new Scene(customControl);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Test Custom Control");
            primaryStage.setWidth(400);
            primaryStage.setHeight(300);
            primaryStage.show();
        }

        public static void main(String[] args) {
            String currentDir = System.getProperty("user.dir");
            System.out.println("Current dir using System:" + currentDir);
            launch(args);
        }
    }

}
