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
 * Implementation of the {@link CityInputBox} interface using a {@link VBox} layout.
 * <p>
 * This component allows users to input a city or address and provides autocomplete suggestions
 * using a {@link ContextMenu} based on the input text.
 * </p>
 *
 * <p>
 * To complete the setup, use the static factory method {@link #create(String, BiConsumer, Function, boolean)}
 * which automatically calls the {@link #initialize(boolean)} method to attach UI elements.
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
     * <p>This constructor sets up the internal components, but it does not attach them to the layout.</p>
     *
     * @param title           The label title shown above the text field.
     * @param onCitySelected  A {@link BiConsumer} callback triggered when a city is selected (description and place ID).
     * @param fetcPredictions A function that provides autocomplete predictions based on user input.
     */
    protected CityInputBoxImpl(final String title, final BiConsumer<String, String> onCitySelected,
                            final Function<String, List<PlaceAutocompletePrediction>> fetcPredictions) {
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
            // aggiungere "!inputChar.equals("")" per evitare di eseguire il codice quando si cancella una lettera
            if (!"\r".equals(inputChar) && !"\t".equals(inputChar)) {
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
        this.setSpacing(10);
        this.getStyleClass().add("city-input-box");
    }


    /**
     * Completes the setup by adding all input components (label and text field) to the layout.
     * <p>This method must be called exactly once after construction. It is invoked automatically
     * by the {@link #create(String, BiConsumer, Function, boolean)} factory method.</p>
     *
     * @param resizeAfterInit if true, the component will be resized after initialization.
     */
    protected void initialize(final boolean resizeAfterInit) {
        this.getChildren().addAll(this.label, this.cityTextField);
        if (resizeAfterInit) {
            this.resize();
        }
    }

    /**
     * Factory method to create and initialize a new {@code CityInputBoxImpl} instance.
     * <p>This is the preferred way to construct the component, as it ensures that
     * the layout and resize options are applied correctly.</p>
     *
     * @param title           The label text shown above the input field.
     * @param onCitySelected  Callback invoked when a prediction is selected.
     * @param fetcPredictions Function to fetch autocomplete predictions based on text input.
     * @param resize          If true, the component will automatically resize to its content.
     * @return A fully initialized {@code CityInputBoxImpl} instance.
     */
    public static CityInputBoxImpl create(final String title, final BiConsumer<String, String> onCitySelected,
                                          final Function<String, List<PlaceAutocompletePrediction>> fetcPredictions,
                                          final boolean resize) {
        final CityInputBoxImpl cityInputBox = new CityInputBoxImpl(title, onCitySelected, fetcPredictions);
        cityInputBox.initialize(resize);
        return cityInputBox;
    }

    /**
     * Updates the {@link ContextMenu} with the given list of autocomplete predictions.
     * Each prediction is shown as a clickable item.
     *
     * @param predictions    The list of {@link PlaceAutocompletePrediction} to display.
     * @param onCitySelected The callback to execute when a prediction is selected.
     */
    private void updateSuggestionsMenu(final List<PlaceAutocompletePrediction> predictions,
                                       final BiConsumer<String, String> onCitySelected) {
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
    protected void setDisableAllInputs(final boolean disable) {
        this.cityTextField.setDisable(disable);
    }
}
