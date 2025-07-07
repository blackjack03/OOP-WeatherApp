package org.app.weathermode.view;

import javafx.scene.Scene;

// CHECKSTYLE: AvoidStarImport OFF
import javafx.scene.control.*;
import javafx.scene.layout.*;
// CHECKSTYLE: AvoidStarImport ON

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import org.app.weathermode.model.locationselector.LocationSelector;
import org.app.weathermode.model.pair.Pair;

import java.util.List;
import java.util.Optional;

/**
 * Classe che mostra un’interfaccia grafica modale per permettere all’utente
 * di cercare e selezionare una località tra quelle disponibili tramite
 * {@link LocationSelector}.
 */
public class LocationSelectorGUI {

    private Integer selectedId;

    /**
     * Avvia la finestra modale per la selezione di una località.
     * Viene aperta una dialog in cui l’utente può digitare almeno due
     * caratteri per filtrare l’elenco delle città; un clic sul nome
     * seleziona la città e chiude la finestra.
     *
     * @param citySelector l’istanza di {@link LocationSelector} usata per
     *                     recuperare le possibili località in base al testo
     *                     inserito dall’utente
     * @return un {@code Optional<Integer>} contenente l’ID della città
     *         selezionata, oppure {@code Optional.empty()} se l’utente
     *         chiude la finestra senza effettuare alcuna selezione
     */
    public Optional<Integer> start(final LocationSelector citySelector) {
        final double rootSpacing = 10.0;
        final double rootPadding = 15.0;
        final String stageTitle = "Scegli la Località";
        final String labelText = "Cerca una città (in inglese):";
        final double resultsSpacing = 5.0;
        final double scrollPrefHeight = 300.0;
        final String scrollStyle = "-fx-background: white;";
        final String exitBtnText = "Esci";
        final int minSearchLength = 2;
        final String noResultsText = "Nessuna città trovata.";
        final String noResultsStyle = "-fx-font-style: italic; -fx-text-fill: gray;";
        final double sceneWidth = 600.0;
        final double sceneHeight = 450.0;
        final String btnBgColor = "#4682B4";
        final String btnTextColor = "white";

        // Configura stage
        final Stage stage = new Stage();
        stage.setTitle(stageTitle);

        // Contenitore radice
        final VBox root = new VBox(rootSpacing);
        root.setPadding(new Insets(rootPadding));

        // Componenti UI
        final Label label = new Label(labelText);
        final TextField searchField = new TextField();
        final VBox resultsBox = new VBox(resultsSpacing);

        final ScrollPane scrollPane = new ScrollPane(resultsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(scrollPrefHeight);
        scrollPane.setStyle(scrollStyle);

        final Button exitButton = new Button(exitBtnText);
        exitButton.setOnAction(e -> stage.close());

        // Logica ricerca
        searchField.setOnKeyReleased(event -> {
            final String text = searchField.getText().trim();
            resultsBox.getChildren().clear();

            if (text.length() < minSearchLength) {
                return;
            }

            final List<Pair<String, Integer>> locations = citySelector.getPossibleLocations(text);
            if (locations.isEmpty()) {
                final Label noResults = new Label(noResultsText);
                noResults.setStyle(noResultsStyle);
                resultsBox.getChildren().add(noResults);
                return;
            }

            for (final Pair<String, Integer> location : locations) {
                final Button cityButton = new Button(location.getX());
                cityButton.setMaxWidth(Double.MAX_VALUE);
                cityButton.setStyle(String.format(
                    "-fx-background-color: %s; -fx-text-fill: %s;", btnBgColor, btnTextColor
                ));
                cityButton.setOnAction(ev -> {
                    selectedId = location.getY();
                    stage.close();
                });
                resultsBox.getChildren().add(cityButton);
            }
        });

        root.getChildren().addAll(label, searchField, scrollPane, exitButton);

        final Scene scene = new Scene(root, sceneWidth, sceneHeight);
        stage.setScene(scene);

        // Finestra modale e bloccante
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        return Optional.ofNullable(selectedId);
    }

}
