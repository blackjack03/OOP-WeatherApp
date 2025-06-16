package org.app.travelmode.model;

import org.app.travelmode.model.travel.api.TravelModeResult;
import org.app.travelmode.model.travel.api.TravelRequest;
import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompletePrediction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Defines the core functionality for a travel planning and route analysis system
 * to check weather conditions that may be encountered during the trip.
 * This interface provides methods for location search, travel request configuration,
 * and route analysis.
 */
public interface TravelModeModel {

    /**
     * Retrieves place predictions based on user input.
     *
     * @param input a {@link String} representing the characters written by the user
     * @return a list of predictions based on the input
     */
    List<PlaceAutocompletePrediction> getPlacePredictions(final String input);

    /**
     * Set the departure location for the travel request.
     *
     * @param departureLocation the name of the departure location
     */
    void setDepartureLocation(final String departureLocation);

    /**
     * Set the PlaceId associated with the starting location and automatically retrieves its timezone.
     *
     * @param departurePlaceId the PlaceId associated with the starting location
     */
    void setDeparturePlaceId(final String departurePlaceId);

    /**
     * Set the arrival location for the travel request.
     *
     * @param arrivalLocation the name of the arrival location
     */
    void setArrivalLocation(final String arrivalLocation);

    /**
     * Set the PlaceId associated with the arrival location for the travel request.
     *
     * @param arrivalPlaceId the PlaceId associated with the arrival location
     */
    void setArrivalPlaceId(final String arrivalPlaceId);

    /**
     * Set the departure time for the travel request.
     *
     * @param departureTime the local time of departure
     */
    void setDepartureTime(final LocalTime departureTime);

    /**
     * Set the departure date for the travel request.
     *
     * @param departureDate the departure date
     */
    void setDepartureDate(final LocalDate departureDate);

    /**
     * Initiates the analysis of directions based on the provided travel request.
     *
     * @param travelRequest the completed travel request to analyze.
     */
    void startDirectionsAnalysis(TravelRequest travelRequest);

    /**
     * Retrieves the main travel mode result.
     *
     * @return a {@link TravelModeResult} object representing the results from analyzing the first route recommended by the routing service
     */
    TravelModeResult getTravelModeMainResult();

    /**
     * Returns a list of alternative route results after analyzing the navigation service's response.
     *
     * @return a {@link List} of {@link TravelModeResult} for alternative routes.
     */
    List<TravelModeResult> getAlternativesResults();

    /**
     * Finalizes and builds the {@link TravelRequest} with all configured parameters.
     *
     * @return the completed {@link TravelRequest} object.
     */
    TravelRequest finalizeTheRequest();
}
