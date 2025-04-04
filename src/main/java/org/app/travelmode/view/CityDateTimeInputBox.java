package org.app.travelmode.view;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;

import java.time.LocalTime;

public interface CityDateTimeInputBox extends CityInputBox {

    /**
     * @return the {@link Spinner} used for entering hours
     */
    Spinner<Integer> getHourSpinner();

    /**
     * @return the {@link Spinner} used for entering minutes
     */
    Spinner<Integer> getMinuteSpinner();

    /**
     * @return the {@link DatePicker} used to insert the departure date
     */
    DatePicker getDatePicker();

    /**
     * @return the selected number in the hour spinner
     */
    int getSelectedHour();

    /**
     * @return the selected number in the minute spinner
     */
    int getSelectedMinute();

    /**
     * Return the selected departure time
     *
     * @return a {@link LocalTime} representing the selected departure time
     */
    LocalTime getSelectedTime();
}
