package org.app.travelmode.view;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class CityDateTimeInputBoxImpl extends CityInputBoxImpl implements CityDateTimeInputBox {

    private static final double SPACING = 5;

    private final Spinner<Integer> hourSpinner;
    private final Spinner<Integer> minuteSpinner;
    private final DatePicker datePicker;
    private final TitledPane dateTimeTitledPane;

    public CityDateTimeInputBoxImpl(final String title, final BiConsumer<String, String> onCitySelected, final Function<String, List<PlaceAutocompletePrediction>> fetcPredictions, final Consumer<LocalDate> onDateSelected, boolean resize) {
        super(title, onCitySelected, fetcPredictions, false);

        // Spinner per le ore (0-23)
        this.hourSpinner = new Spinner<>();
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 12));

        // Spinner per i minuti (0-59) con incremento di 5 minuti
        this.minuteSpinner = new Spinner<>();
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 5));

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
                    setStyle("-fx-background-color: #f4f4f4; -fx-text-fill: #b0b0b0; -fx-opacity: 0.6;");
                } else {
                    setStyle("-fx-background-color: #ffffff; -fx-text-fill: #2c3e50;");
                    setOnMouseEntered(e -> setStyle("-fx-background-color: #d1e8ff; -fx-text-fill: #2c3e50;"));
                    setOnMouseExited(e -> setStyle("-fx-background-color: #ffffff; -fx-text-fill: #2c3e50;"));
                }
            }
        });
        datePicker.setOnAction(event -> onDateSelected.accept(datePicker.getValue()));
        datePicker.setShowWeekNumbers(false);
        datePicker.setEditable(false);

        final HBox timeHBox = new HBox();
        timeHBox.getChildren().addAll(hourSpinner, minuteSpinner);

        final VBox dateTimeVBox = new VBox(SPACING);
        dateTimeVBox.getChildren().addAll(timeHBox, datePicker);

        dateTimeTitledPane = new TitledPane("Personalizza data e ora", dateTimeVBox);
        dateTimeTitledPane.setExpanded(false);

        this.getChildren().add(dateTimeTitledPane);

        if (resize) {
            this.resize();
        }
    }

    @Override
    protected double computeRequiredHeight() {
        return super.computeRequiredHeight() + this.dateTimeTitledPane.getHeight();
    }

    @Override
    public Spinner<Integer> getHourSpinner() {
        return this.hourSpinner;
    }

    @Override
    public Spinner<Integer> getMinuteSpinner() {
        return this.minuteSpinner;
    }

    @Override
    public DatePicker getDatePicker() {
        return this.datePicker;
    }

    @Override
    public int getSelectedHour() {
        return this.hourSpinner.getValue();
    }

    @Override
    public int getSelectedMinute() {
        return this.minuteSpinner.getValue();
    }

}
