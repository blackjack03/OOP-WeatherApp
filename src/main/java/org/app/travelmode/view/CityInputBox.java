package org.app.travelmode.view;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public interface CityInputBox {

    /**
     * @return the {@link Label} used to display the box title
     */
    Label getTitleLabel();

    /**
     * @return the {@link TextField} used to enter the city
     */
    TextField getCityTextField();

    /**
     * Disable all input fields in the component
     */
    void disableAllInputs();

    /**
     * Activate all input fields of the component
     */
    void activateAllInputs();
}
