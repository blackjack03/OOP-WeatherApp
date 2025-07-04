package org.app.travelmode.view;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * {@code CityInputBox} defines the interface for a user input component
 * that allows the selection or entry of a city or address.
 * <p>
 * It provides access to the main input UI elements (label and text field)
 * and allows the input to be enabled or disabled programmatically.
 * </p>
 *
 * <p>This interface is typically implemented by JavaFX components
 * that support city search or autocomplete functionalities.</p>
 */
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
