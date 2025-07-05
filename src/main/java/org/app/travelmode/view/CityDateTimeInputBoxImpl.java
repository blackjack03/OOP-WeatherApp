package org.app.travelmode.view;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TitledPane;
import javafx.scene.control.DateCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompletePrediction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * {@code CityDateTimeInputBoxImpl} extends {@link CityInputBoxImpl} to provide additional
 * UI components for selecting a date and time alongside the city input field.
 * <p>
 * This JavaFX component provides:
 * <ul>
 *   <li>An input field with autocomplete for city or address</li>
 *   <li>A collapsible section to select the date and time</li>
 *   <li>Date constraints (only selectable within a 7-day range from today)</li>
 *   <li>Time selection via hour and minute spinners</li>
 * </ul>
 *
 * <p>
 * To ensure proper initialization of the full UI, the instance should be created
 * using the static factory method {@link #create(String, BiConsumer, Function, Consumer, boolean)}.
 * The constructor alone does not add the date/time section to the UI; this is completed via {@link #initialize(boolean)}.
 */
public class CityDateTimeInputBoxImpl extends CityInputBoxImpl implements CityDateTimeInputBox {

    private static final double SPACING = 5;
    private static final String TITLED_PANE_PROMPT = "Personalizza data e ora";
    private static final int MAX_HOURS = 23;
    private static final int MAX_MINUTES = 59;
    private static final int MIN_HOURS = 0;
    private static final int MIN_MINUTES = 0;
    private static final int INITIAL_HOUR = 12;
    private static final int INITIAL_MINUTE = 0;
    private static final int MINUTES_TO_INCREMENT = 5;

    private final Spinner<Integer> hourSpinner;
    private final Spinner<Integer> minuteSpinner;
    private final DatePicker datePicker;
    private final TitledPane dateTimeTitledPane;

    /**
     * Protected constructor. Initializes the city input and date/time controls,
     * but does not add the date/time UI section to the layout.
     * Use {@link #create(String, BiConsumer, Function, Consumer, boolean)} for proper instantiation.
     *
     * @param title           The label shown above the input field.
     * @param onCitySelected  A {@link BiConsumer} invoked when a city is selected (description and place ID).
     * @param fetcPredictions A function that takes user input and returns a list of {@link PlaceAutocompletePrediction}.
     * @param onDateSelected  A {@link Consumer} that receives the selected {@link LocalDate} from the date picker.
     */
    protected CityDateTimeInputBoxImpl(final String title, final BiConsumer<String, String> onCitySelected,
                                       final Function<String, List<PlaceAutocompletePrediction>> fetcPredictions,
                                       final Consumer<LocalDate> onDateSelected) {
        super(title, onCitySelected, fetcPredictions);

        this.hourSpinner = new Spinner<>();
        hourSpinner.setValueFactory(new SpinnerValueFactory
                .IntegerSpinnerValueFactory(MIN_HOURS, MAX_HOURS, INITIAL_HOUR));
        hourSpinner.getStyleClass().add("spinner-custom");

        this.minuteSpinner = new Spinner<>();
        minuteSpinner.setValueFactory(new SpinnerValueFactory
                .IntegerSpinnerValueFactory(MIN_MINUTES, MAX_MINUTES, INITIAL_MINUTE, MINUTES_TO_INCREMENT));
        minuteSpinner.getStyleClass().add("spinner-custom");

        this.datePicker = new DatePicker();
        final LocalDate oggi = LocalDate.now();
        final LocalDate start = oggi;
        final LocalDate end = oggi.plusDays(6);
        datePicker.setDayCellFactory((dP) -> new DateCell() {
            @Override
            public void updateItem(final LocalDate date, final boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(start) || date.isAfter(end)) {
                    setDisable(true);
                    getStyleClass().add("date-picker-disabled-cell");
                } else {
                    getStyleClass().add("date-picker-enabled-cell");
                }
            }
        });
        datePicker.setOnAction(event -> onDateSelected.accept(datePicker.getValue()));
        datePicker.setShowWeekNumbers(false);
        datePicker.setEditable(false);
        datePicker.getStyleClass().add("date-picker-custom");

        final HBox timeHBox = new HBox();
        timeHBox.getChildren().addAll(hourSpinner, minuteSpinner);

        final VBox dateTimeVBox = new VBox(SPACING);
        dateTimeVBox.getChildren().addAll(timeHBox, datePicker);

        dateTimeTitledPane = new TitledPane(TITLED_PANE_PROMPT, dateTimeVBox);
        dateTimeTitledPane.setExpanded(false);
        dateTimeTitledPane.getStyleClass().add("titled-pane-custom");
    }

    /**
     * Completes the initialization of the component by adding the date/time UI section
     * to the main layout and triggering an optional resize.
     * <p>This method is called once by the factory method {@code create(...)} after construction.</p>
     *
     * @param resizeAfterInit whether to apply resizing after layout update
     */
    @Override
    protected void initialize(final boolean resizeAfterInit) {
        super.initialize(false);
        this.getChildren().add(dateTimeTitledPane);
        if (resizeAfterInit) {
            this.resize();
        }
    }

    /**
     * Factory method to create and fully initialize a {@code CityDateTimeInputBoxImpl}.
     * <p>This is the recommended way to instantiate the component.</p>
     *
     * @param title           The label above the input field.
     * @param onCitySelected  A callback triggered when a city is selected.
     * @param fetcPredictions A function to fetch autocomplete predictions.
     * @param onDateSelected  A callback triggered when a date is selected.
     * @param resize          Whether the component should be resized after initialization.
     * @return A fully initialized {@code CityDateTimeInputBoxImpl} instance.
     */
    public static CityDateTimeInputBoxImpl create(final String title, final BiConsumer<String, String> onCitySelected,
                                                  final Function<String, List<PlaceAutocompletePrediction>> fetcPredictions,
                                                  final Consumer<LocalDate> onDateSelected, final boolean resize) {
        final CityDateTimeInputBoxImpl cityDateTimeInputBox = new CityDateTimeInputBoxImpl(title, onCitySelected,
                fetcPredictions, onDateSelected);
        cityDateTimeInputBox.initialize(resize);
        return cityDateTimeInputBox;
    }

    /**
     * Computes the required height for the component, including the city input box and
     * the optional date/time pane if expanded.
     *
     * @return The total required height.
     */
    @Override
    protected double computeRequiredHeight() {
        return super.computeRequiredHeight() + this.dateTimeTitledPane.getHeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Spinner<Integer> getHourSpinner() {
        return this.hourSpinner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Spinner<Integer> getMinuteSpinner() {
        return this.minuteSpinner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatePicker getDatePicker() {
        return this.datePicker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSelectedHour() {
        return this.hourSpinner.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSelectedMinute() {
        return this.minuteSpinner.getValue();
    }

    /**
     * {@inheritDoc}
     * <p>If the date/time section is collapsed, the current system time is returned instead.</p>
     */
    @Override
    public LocalTime getSelectedTime() {
        if (this.dateTimeTitledPane.isExpanded()) {
            return LocalTime.of(this.hourSpinner.getValue(), this.minuteSpinner.getValue());
        }
        return LocalTime.now();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDateTimePersonalizationClosed() {
        return !this.dateTimeTitledPane.isExpanded();
    }

    /**
     * Disables all input components (city field, spinners, and date picker).
     */
    @Override
    public void disableAllInputs() {
        this.setDisableAllInputs(true);
    }

    /**
     * Enables all input components (city field, spinners, and date picker).
     */
    @Override
    public void activateAllInputs() {
        this.setDisableAllInputs(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setDisableAllInputs(final boolean disable) {
        super.setDisableAllInputs(disable);
        this.hourSpinner.setDisable(disable);
        this.minuteSpinner.setDisable(disable);
        this.datePicker.setDisable(disable);
    }
}
