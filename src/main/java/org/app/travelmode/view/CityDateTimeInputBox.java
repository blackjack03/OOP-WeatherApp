package org.app.travelmode.view;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;

import java.time.LocalTime;

public interface CityDateTimeInputBox extends CityInputBox {

    /**
     * Returns the hour spinner component.
     *
     * @return A {@link Spinner} for selecting the hour (0–23).
     */
    Spinner<Integer> getHourSpinner();

    /**
     * Returns the minute spinner component.
     *
     * @return A {@link Spinner} for selecting the minute (0–59).
     */
    Spinner<Integer> getMinuteSpinner();

    /**
     * Returns the {@link DatePicker} component used for selecting a date.
     *
     * @return The {@link DatePicker} with restricted selectable range.
     */
    DatePicker getDatePicker();

    /**
     * Gets the selected hour from the hour spinner.
     *
     * @return The selected hour as an integer.
     */
    int getSelectedHour();

    /**
     * Gets the selected minute from the minute spinner.
     *
     * @return The selected minute as an integer.
     */
    int getSelectedMinute();

    /**
     * Returns the selected {@link LocalTime} based on spinner values.
     *
     * @return The selected or current {@link LocalTime}.
     */
    LocalTime getSelectedTime();
}
