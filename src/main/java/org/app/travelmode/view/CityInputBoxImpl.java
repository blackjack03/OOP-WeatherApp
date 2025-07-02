package org.app.travelmode.view;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompletePrediction;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * {@code CityInputBoxImpl} is a JavaFX UI component that allows the user to input a city or address,
 * with autocomplete suggestions powered by a list of {@link PlaceAutocompletePrediction}.
 * <p>
 * This class extends {@link VBox} and implements the {@link CityInputBox} interface, offering a labeled
 * input box with live suggestions that appear in a context menu as the user types.
 * </p>
 */
public class CityInputBoxImpl extends VBox implements CityInputBox {

    private static final double TEXT_FIELD_MIN_WIDTH = 200;
    private static final double MAX_WIDTH = 250;
    private static final String INPUT_PROMPT = "Inserire la città o l'indirizzo";

    private final Label label;
    private final TextField cityTextField;
    private final ContextMenu suggestionsMenu;

    /**
     * Constructs a new {@code CityInputBoxImpl}.
     *
     * @param title           The label title shown above the text field.
     * @param onCitySelected  A {@link BiConsumer} that accepts the selected city's description and place ID.
     * @param fetcPredictions A function that fetches autocomplete predictions based on user input.
     * @param resize          If true, the component will resize to its preferred width and height.
     */
    public CityInputBoxImpl(final String title, final BiConsumer<String, String> onCitySelected,
                            final Function<String, List<PlaceAutocompletePrediction>> fetcPredictions, boolean resize) {
        super();

        this.label = new Label(title);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        this.label.getStyleClass().add("label-title");

        this.cityTextField = new TextField();
        this.suggestionsMenu = new ContextMenu();
        this.suggestionsMenu.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                event.consume();
            }
        });

        this.cityTextField.setMinWidth(TEXT_FIELD_MIN_WIDTH);
        this.cityTextField.setPromptText(INPUT_PROMPT);
        this.cityTextField.getStyleClass().add("city-input-text-field");
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
        this.getStyleClass().add("city-input-box");

        if (resize) {
            resize();
        }
    }

    /**
     * Updates the {@link ContextMenu} with the given list of autocomplete predictions.
     * Each prediction is shown as a clickable item.
     *
     * @param predictions    The list of {@link PlaceAutocompletePrediction} to display.
     * @param onCitySelected The callback to execute when a prediction is selected.
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

    /**
     * Resizes the component by setting the maximum width and height.
     */
    protected void resize() {
        this.setMaxSize(MAX_WIDTH, computeRequiredHeight());
    }

    /**
     * Computes the required height of the component based on its children.
     *
     * @return The calculated height of the component.
     */
    protected double computeRequiredHeight() {
        return this.label.getHeight() + this.cityTextField.getHeight() + getSpacing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Label getTitleLabel() {
        return this.label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextField getCityTextField() {
        return this.cityTextField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disableAllInputs() {
        this.setDisableAllInputs(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activateAllInputs() {
        this.setDisableAllInputs(false);
    }

    /**
     * Sets the disabled state of all input components.
     *
     * @param disable If true, disables the inputs; otherwise, enables them.
     */
    protected void setDisableAllInputs(boolean disable) {
        this.cityTextField.setDisable(disable);
    }
}
