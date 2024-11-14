package org.app.travelmode.view;

public interface TravelModeView {

    /**
     * This method is called before the UI is used. It should finalize its status.
     */
    void start();

    /**
     * Some unexpected error occurred in the Controller, and the user should be informed.
     *
     * @param message the message associated with the error.
     */
    void displayError(String message);

}
