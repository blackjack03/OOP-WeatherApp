package org.app.travelmode.view;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;

import java.time.LocalTime;

/**
 * {@code CityDateTimeInputBox} extends {@link CityInputBox} and adds support
 * for date and time input components.
 * <p>
 *
 * <p>It is typically implemented by JavaFX components where users need to
 * specify both a location and a specific time.</p>
 */
public interface CityDateTimeInputBox extends CityInputBox {

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

    /**
     * Checks if the date/time customization section is closed (collapsed).
     *
     * <p>This method determines whether the user has access to the custom date and time selection.
     *
     * @return {@code true} if the date/time section is collapsed (not expanded),
     * {@code false} if it is expanded and available for user input
     */
    boolean isDateTimePersonalizationClosed();
}
