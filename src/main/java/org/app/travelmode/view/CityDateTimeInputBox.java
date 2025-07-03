package org.app.travelmode.view;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;

import java.time.LocalTime;

/**
 * {@code CityDateTimeInputBox} extends {@link CityInputBox} and adds support
 * for date and time input components.
 * <p>
 * This interface defines methods for accessing UI elements used to select a date,
 * hour, and minute, as well as for retrieving the corresponding selected time.
 * </p>
 *
 * <p>It is typically implemented by JavaFX components where users need to
 * specify both a location and a specific time.</p>
 */
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
