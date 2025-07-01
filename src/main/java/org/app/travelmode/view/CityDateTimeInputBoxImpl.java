package org.app.travelmode.view;

import javafx.scene.control.*;
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
 * {@code CityDateTimeInputBoxImpl} is a UI component that extends {@link CityInputBoxImpl}
 * and adds support for selecting a date and time alongside the city input.
 * <p>
 * This JavaFX component provides:
 * <ul>
 *   <li>An input field with autocomplete for city or address</li>
 *   <li>A collapsible section to select the date and time</li>
 *   <li>Date constraints (only selectable within a 7-day range from today)</li>
 *   <li>Time selection via hour and minute spinners</li>
 * </ul>
 *
 */
public class CityDateTimeInputBoxImpl extends CityInputBoxImpl implements CityDateTimeInputBox {

    private static final double SPACING = 5;
    private static final String TITLED_PANE_PROMPT = "Personalizza data e ora";

    private final Spinner<Integer> hourSpinner;
    private final Spinner<Integer> minuteSpinner;
    private final DatePicker datePicker;
    private final TitledPane dateTimeTitledPane;

    /**
     * Constructs a new {@code CityDateTimeInputBoxImpl}.
     *
     * @param title           The label shown above the input field.
     * @param onCitySelected  A {@link BiConsumer} callback executed when a city is selected (description and place ID).
     * @param fetcPredictions A function that takes user input and returns a list of {@link PlaceAutocompletePrediction}.
     * @param onDateSelected  A {@link Consumer} that receives the selected {@link LocalDate} when changed.
     * @param resize          If true, enables component resizing based on content.
     */
    public CityDateTimeInputBoxImpl(final String title, final BiConsumer<String, String> onCitySelected,
                                    final Function<String, List<PlaceAutocompletePrediction>> fetcPredictions,
                                    final Consumer<LocalDate> onDateSelected, boolean resize) {
        super(title, onCitySelected, fetcPredictions, false);

        // Spinner per le ore (0-23)
        this.hourSpinner = new Spinner<>();
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));
        hourSpinner.getStyleClass().add("spinner-custom");

        // Spinner per i minuti (0-59) con incremento di 5 minuti
        this.minuteSpinner = new Spinner<>();
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 5));
        minuteSpinner.getStyleClass().add("spinner-custom");

        this.datePicker = new DatePicker();
        final LocalDate oggi = LocalDate.now();
        final LocalDate start = oggi;
        final LocalDate end = oggi.plusDays(6);
        datePicker.setDayCellFactory((dP) -> new DateCell() {
            @Override
            public void updateItem(final LocalDate date, boolean empty) {
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

        this.getChildren().add(dateTimeTitledPane);

        if (resize) {
            this.resize();
        }
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
     * If the date/time section is collapsed, the current system time is returned instead.
     */
    @Override
    public LocalTime getSelectedTime() {
        if (this.dateTimeTitledPane.isExpanded()) {
            return LocalTime.of(this.hourSpinner.getValue(), this.minuteSpinner.getValue());
        }
        return LocalTime.now();
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
    protected void setDisableAllInputs(boolean disable) {
        super.setDisableAllInputs(disable);
        this.hourSpinner.setDisable(disable);
        this.minuteSpinner.setDisable(disable);
        this.datePicker.setDisable(disable);
    }
}
