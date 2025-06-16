package org.app.travelmode.controller;

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
     * Retrieves location suggestions based on the user's input text.
     * This method provides autocomplete functionality for location search.
     *
     * @param input a {@link String} representing the characters written by the user
     * @return a list of {@link PlaceAutocompletePrediction} matching the input text
     */
    List<PlaceAutocompletePrediction> getPlacePredictions(final String input);

    /**
     * Set the departure location
     *
     * @param departureLocation the name or address of the departure location
     */
    void setDepartureLocation(final String departureLocation);

    /**
     * Set the PlaceId associated with the starting location
     *
     * @param departurePlaceId the PlaceId associated with the starting location
     */
    void setDeparturePlaceId(final String departurePlaceId);

    /**
     * Set the arrival location
     *
     * @param arrivalLocation the name or address of the arrival location
     */
    void setArrivalLocation(final String arrivalLocation);

    /**
     * Set the PlaceId associated with the arrival location
     *
     * @param arrivalPlaceId the PlaceId associated with the arrival location
     */
    void setArrivalPlaceId(final String arrivalPlaceId);

    /**
     * Set the departure time
     *
     * @param departureTime the departure time
     */
    void setDepartureTime(final LocalTime departureTime);

    /**
     * Set the departure date
     *
     * @param departureDate the departure date
     */
    void setDepartureDate(final LocalDate departureDate);

    /**
     * Start the analysis of possible routes between the inserted places
     */
    void startRouteAnalysis();

    /**
     * Analyzes the alternative routes between the two entered locations and inserts the results into the view.
     */
    void computeAlternativeResults();
}
