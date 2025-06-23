package org.app.travelmode.model.core;

import org.app.travelmode.model.travel.api.TravelModeResult;
import org.app.travelmode.model.travel.api.TravelRequest;
import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompletePrediction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

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
     * Retrieves the alternative routes available for the current travel request.
     *
     * <p>Alternative routes are additional possible paths to reach the destination,
     * different from the main route returned by {@link #getTravelModeMainResult()}.
     *
     * @return an {@link Optional} containing a {@link List} of {@link TravelModeResult}s
     * representing the alternative routes. Returns an empty Optional if no
     * alternative routes are available.
     * @throws IllegalStateException if called before {@link #startDirectionsAnalysis(TravelRequest)}
     *                               or if the directions analysis failed
     */
    Optional<List<TravelModeResult>> getAlternativesResults();

    /**
     * Finalizes and builds the {@link TravelRequest} with all configured parameters.
     *
     * @return the completed {@link TravelRequest} object.
     */
    TravelRequest finalizeTheRequest();
}
