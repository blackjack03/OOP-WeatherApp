package org.app.travelmode.view;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public interface CityInputBox {

    /**
     * Returns the label component used as the title of the input box.
     *
     * @return The {@link Label} component.
     */
    Label getTitleLabel();

    /**
     * Returns the text field used to input the city or address.
     *
     * @return The {@link TextField} component.
     */
    TextField getCityTextField();

    /**
     * Disables the input field to prevent user interaction.
     */
    void disableAllInputs();

    /**
     * Enables the input field to allow user interaction.
     */
    void activateAllInputs();
}
