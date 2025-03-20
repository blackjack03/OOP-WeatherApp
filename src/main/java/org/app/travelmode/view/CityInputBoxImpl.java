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

public class CityInputBoxImpl extends VBox implements CityInputBox {

    private static final double TEXT_FIELD_MIN_WIDTH = 200;
    private static final double MAX_WIDTH = 250;
    private static final String INPUT_PROMPT = "Inserire la città o l'indirizzo";

    private final Label label;
    private final TextField cityTextField;
    private final ContextMenu suggestionsMenu;

    public CityInputBoxImpl(final String title, final BiConsumer<String, String> onCitySelected, final Function<String, List<PlaceAutocompletePrediction>> fetcPredictions, boolean resize) {
        super();
        this.label = new Label(title);
        this.label.setStyle(
                "-fx-font-family: 'Arial Rounded MT Bold', sans-serif;" +
                        "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2980b9;" //+
                        //"-fx-padding: 4px, 4px, 0px, 4px;"
        );
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        this.cityTextField = new TextField();
        this.suggestionsMenu = new ContextMenu();
        this.cityTextField.setMinWidth(TEXT_FIELD_MIN_WIDTH);
        this.cityTextField.setPromptText(INPUT_PROMPT);

        this.cityTextField.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-border-color: #3498db;" +
                        "-fx-border-width: 2px;" +
                        "-fx-background-color: #ecf0f1;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-radius: 10px;"
        );
        this.cityTextField.setOnMouseEntered(e -> this.cityTextField.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-border-color: #1abc9c;" +
                        "-fx-border-width: 2px;" +
                        "-fx-background-color: #e3f2fd;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-radius: 10px;"
        ));
        this.cityTextField.setOnMouseExited(e -> this.cityTextField.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #2c3e50;" +
                        "-fx-border-color: #3498db;" +
                        "-fx-border-width: 2px;" +
                        "-fx-background-color: #ecf0f1;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-radius: 10px;"
        ));

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
        this.setSpacing(10);
        this.setStyle(
                "-fx-border-color: #2980b9;" +
                        "-fx-border-width: 2px;" +
                        "-fx-padding: 15px;" +
                        "-fx-background-color: #ffffff;" +
                        "-fx-border-radius: 15px;" +
                        "-fx-background-radius: 15px;"
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
            final MenuItem menuItem = new MenuItem(prediction.getDescription());
            menuItem.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50; -fx-padding: 5px;");
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

    @Override
    public Label getTitleLabel() {
        return this.label;
    }

    @Override
    public TextField getCityTextField() {
        return this.cityTextField;
    }

    @Override
    public void disableAllInputs() {
        this.setDisableAllInputs(true);
    }

    @Override
    public void activateAllInputs() {
        this.setDisableAllInputs(false);
    }

    protected void setDisableAllInputs(boolean disable) {
        this.cityTextField.setDisable(disable);
    }
}
