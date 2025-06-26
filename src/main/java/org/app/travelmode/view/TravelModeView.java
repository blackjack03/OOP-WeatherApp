package org.app.travelmode.view;

import javafx.scene.Parent;
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

    /**
     * Allows you to view the result of the analysis of a route.
     *
     * @param meteoScore  The score related to the weather conditions of the route.
     * @param description A short description of the route.
     * @param duration    The duration of the journey.
     * @param arrivalDate The expected arrival date.
     * @param arrivalTime The expected arrival time.
     * @param mapImage    An image of a map representing the route.
     */
    void displayResult(int meteoScore, String description, String duration, String arrivalDate, String arrivalTime, Image mapImage);

    Parent getRootView();
}
