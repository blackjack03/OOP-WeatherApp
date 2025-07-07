package org.app.travelmode.view;

/**
 * {@code CityInputBox} defines the interface for a user input component
 * that allows the selection or entry of a city or address.
 * <p>
 *
 * <p>This interface is typically implemented by JavaFX components
 * that support city search or autocomplete functionalities.</p>
 */
public interface CityInputBox {

    /**
     * Disables the input field to prevent user interaction.
     */
    void disableAllInputs();

    /**
     * Enables the input field to allow user interaction.
     */
    void activateAllInputs();
}
