package org.app.travelmode.view;

import javafx.scene.image.Image;

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

    void displayResult(String meteoScore, String description, String duration, String arrivalTime, Image mapImage);
}
