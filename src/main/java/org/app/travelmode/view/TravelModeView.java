package org.app.travelmode.view;

import javafx.scene.Parent;
import javafx.scene.image.Image;

/**
 * {@code TravelModeView} defines the interface for the complete user interface
 * of the travel mode.
 * <p>
 * This interface includes both the input components (such as city, date, and time selection)
 * and the output components (such as weather score, travel duration, and route map).
 * </p>
 *
 * <p>It is usually implemented by a JavaFX view class that allows users to enter travel information
 * and then displays the results after the analysis is completed.</p>
 */
public interface TravelModeView {

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
    void displayResult(int meteoScore, String description, String duration,
                       String arrivalDate, String arrivalTime, Image mapImage);

    /**
     * Returns the root view container for this travel mode interface.
     *
     * <p>The root view contains all UI components organized in a vertical layout.
     *
     * @return the root {@link Parent} node containing all view components
     */
    Parent getRootView();
}
