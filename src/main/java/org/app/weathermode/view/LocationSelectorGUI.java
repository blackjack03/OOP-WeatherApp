package org.app.weathermode.view;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;

import org.app.weathermode.model.LocationSelector;
import org.app.weathermode.model.Pair;

import java.util.List;
import java.util.Optional;

/**
 * GUI per la selezione di una località che restituisce un Optional<Integer>
 * contenente l'ID della città scelta o Optional.empty() se l'utente chiude
 * la finestra senza effettuare alcuna selezione.
 */
public class LocationSelectorGUI {

    private Integer selectedId = null;

    public Optional<Integer> start(final LocationSelector citySelector) {
        final Stage stage = new Stage();
        stage.setTitle("Scegli la Località");

        final VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        final Label label = new Label("Cerca una città (in inglese):");
        final TextField searchField = new TextField();
        final VBox resultsBox = new VBox(5);

        final ScrollPane scrollPane = new ScrollPane(resultsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background: white;");

        final Button exitButton = new Button("Esci");
        exitButton.setOnAction(e -> stage.close());

        searchField.setOnKeyReleased(event -> {
            final String text = searchField.getText().trim();
            resultsBox.getChildren().clear();

            if (text.length() < 2) {
                return;
            }

            final List<Pair<String, Integer>> locations = citySelector.getPossibleLocations(text);
            if (locations.isEmpty()) {
                Label noResults = new Label("Nessuna città trovata.");
                noResults.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
                resultsBox.getChildren().add(noResults);
                return;
            }

            for (final Pair<String, Integer> location : locations) {
                final Button cityButton = new Button(location.getX());
                cityButton.setMaxWidth(Double.MAX_VALUE);
                cityButton.setStyle("-fx-background-color: #4682B4; -fx-text-fill: white;");
                cityButton.setOnAction(ev -> {
                    selectedId = location.getY();
                    stage.close();
                });
                resultsBox.getChildren().add(cityButton);
            }
        });

        root.getChildren().addAll(label, searchField, scrollPane, exitButton);

        final Scene scene = new Scene(root, 600, 450);
        stage.setScene(scene);

        // Rende la finestra modale e bloccante
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        return Optional.ofNullable(selectedId);
    }

}
