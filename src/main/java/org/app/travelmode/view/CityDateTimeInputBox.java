package org.app.travelmode.view;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;

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
}
