package org.app.travelmode.view;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CityInputBox extends VBox {

    private static final double TEXT_FIELD_MIN_WIDTH = 200;
    private static final double MAX_WIDTH = 220;

    private final Label label;
    private final TextField cityTextField;
    private final ContextMenu suggestionsMenu;

    public CityInputBox(final String title, final BiConsumer<String, String> onCitySelected, final Function<String, List<PlaceAutocompletePrediction>> fetcPredictions, boolean resize) {
        super();
        this.label = new Label(title);
        this.cityTextField = new TextField();
        this.suggestionsMenu = new ContextMenu();
        this.cityTextField.setMinWidth(TEXT_FIELD_MIN_WIDTH);
        this.cityTextField.setPromptText("Inserire la città o l'indirizzo");
        this.cityTextField.setOnKeyTyped(event -> {
            final String inputChar = event.getCharacter();
            if (!inputChar.equals("\r") && !inputChar.equals("\t")) { // aggiungere "!inputChar.equals("")" per evitare di eseguire il codice quando si cancella una lettera
                final String actualText = this.cityTextField.getText();
                if (actualText.length() >= 3) {
                    final List<PlaceAutocompletePrediction> placePredictions = fetcPredictions.apply(actualText);
                    this.updateSuggestionsMenu(placePredictions, onCitySelected);
                    if (!this.suggestionsMenu.isShowing()) {
                        this.suggestionsMenu.show(this.cityTextField, Side.BOTTOM, 0, 0);
                    }
                } else {
                    this.suggestionsMenu.hide();
                }
            }
        });
        this.setAlignment(Pos.CENTER_LEFT);
        this.getChildren().addAll(this.label, this.cityTextField);
        this.setStyle(
                "-fx-border-color: black;" +                // Colore del bordo
                        "-fx-border-width: 2px;" +          // Larghezza del bordo
                        "-fx-padding: 10px;" +               // Spazio interno
                        "-fx-background-color: white;" +    // Colore di sfondo
                        "-fx-border-radius: 15px; " +        // Arrotondamento del bordo
                        "-fx-background-radius: 15px;"      // Arrotondamento dello sfondo
        );
        if (resize) {
            resize();
        }
    }

    /**
     * Update a {@link ContextMenu} with the new {@link PlaceAutocompletePrediction}
     *
     * @param predictions The list of new predictions
     */
    private void updateSuggestionsMenu(final List<PlaceAutocompletePrediction> predictions, final BiConsumer<String, String> onCitySelected) {
        this.suggestionsMenu.getItems().clear();
        for (final PlaceAutocompletePrediction prediction : predictions) {
            final MenuItem menuItem = new MenuItem();
            menuItem.setText(prediction.getDescription());
            menuItem.setOnAction(event -> {
                this.cityTextField.setText(prediction.getDescription());
                this.suggestionsMenu.hide();
                onCitySelected.accept(prediction.getDescription(), prediction.getPlaceId());
            });
            this.suggestionsMenu.getItems().add(menuItem);
        }
    }

    protected void resize() {
        this.setMaxSize(MAX_WIDTH, computeRequiredHeight());
    }

    protected double computeRequiredHeight() {
        return this.label.getHeight() + this.cityTextField.getHeight() + getSpacing();
    }

}
