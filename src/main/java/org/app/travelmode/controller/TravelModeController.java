package org.app.travelmode.controller;

import javafx.scene.Parent;
import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompletePrediction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Interface defining the contract for the Travel Mode application controller.
 * This controller manages the travel planning functionality, including location
 * selection, route analysis with weather detection, and travel time calculations.
 */
public interface TravelModeController {

    /**
     * Initializes and starts the travel mode functionality.
     *
     * <p>This method should be called when switching to travel mode to ensure
     * all necessary resources and services are properly initialized.
     */
    void startTravelMode();

    /**
     * Retrieves location suggestions based on the user's input text.
     * This method provides autocomplete functionality for location search.
     *
     * @param input a {@link String} representing the characters written by the user
     * @return a list of {@link PlaceAutocompletePrediction} matching the input text
     */
    List<PlaceAutocompletePrediction> getPlacePredictions(String input);

    /**
     * Set the departure location.
     *
     * @param departureLocation the name or address of the departure location
     */
    void setDepartureLocation(String departureLocation);

    /**
     * Set the PlaceId associated with the starting location.
     *
     * @param departurePlaceId the PlaceId associated with the starting location
     */
    void setDeparturePlaceId(String departurePlaceId);

    /**
     * Set the arrival location.
     *
     * @param arrivalLocation the name or address of the arrival location
     */
    void setArrivalLocation(String arrivalLocation);

    /**
     * Set the PlaceId associated with the arrival location.
     *
     * @param arrivalPlaceId the PlaceId associated with the arrival location
     */
    void setArrivalPlaceId(String arrivalPlaceId);

    /**
     * Set the departure time.
     *
     * @param departureTime the departure time
     */
    void setDepartureTime(LocalTime departureTime);

    /**
     * Set the departure date.
     *
     * @param departureDate the departure date
     */
    void setDepartureDate(LocalDate departureDate);

    /**
     * Start the analysis of possible routes between the inserted places.
     *
     * @return true if the analysis was successful, false otherwise
     */
    boolean startRouteAnalysis();

    /**
     * Analyzes the alternative routes between the two entered locations and inserts the results into the view.
     */
    void computeAlternativeResults();

    /**
     * Displays an error message to the user through the GUI.
     *
     * <p>This method should be used to show critical errors that prevent
     * normal operation of the travel mode functionality.
     *
     * @param title   the title of the error message dialog
     * @param message the detailed error message to be displayed
     */
    void showErrorOnGUI(String title, String message);

    /**
     * Displays a warning message to the user through the GUI.
     *
     * <p>This method should be used to show non-critical warnings that
     * don't prevent the main functionality but require user attention.
     *
     * @param title   the title of the warning message dialog
     * @param message the detailed warning message to be displayed
     */
    void showWarningOnGUI(String title, String message);

    /**
     * Retrieves the root view node for the travel mode interface.
     *
     * <p>This method provides access to the main visual component that contains
     * all travel mode UI elements. It should be used when the travel mode
     * view needs to be displayed or manipulated.
     *
     * @return the root Parent node containing all travel mode UI components
     */
    Parent gatTraveleModeView();

    /**
     * Retrieves the root view node of the main application.
     *
     * <p>This method provides access to the application's main view hierarchy.
     * It should be used when travel mode components need to interact with
     * or modify the main application view.
     *
     * @return the root Parent node of the main application
     */
    Parent requestAppViewRootNode();
}
