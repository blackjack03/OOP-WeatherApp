package org.app.travelmode.model.core;

import org.app.travelmode.model.exception.DirectionsApiException;
import org.app.travelmode.model.exception.TravelRequestException;
import org.app.travelmode.model.exception.WeatherDataException;
import org.app.travelmode.model.travel.api.TravelModeResult;
import org.app.travelmode.model.travel.api.TravelRequest;
import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompletePrediction;

import java.io.IOException;
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
     * Initializes the model and prepares it for handling travel-related operations.
     *
     * <p>This method must be called before any other operations can be performed.
     * It sets up:
     * <ul>
     *     <li>API clients for location services</li>
     *     <li>Place prediction services</li>
     *     <li>Other necessary resources for route analysis</li>
     * </ul>
     *
     * <p>The model will not be fully functional until this method has been called.
     *
     * @throws IllegalStateException if the initialization of any required services fails
     */
    void start();

    /**
     * Retrieves place predictions based on user input.
     *
     * @param input a {@link String} representing the characters written by the user
     * @return a list of predictions based on the input
     * @throws IOException if an error occurs while communicating with the service providing the predictions,
     *                     or if an error occurs while decoding the response.
     */
    List<PlaceAutocompletePrediction> getPlacePredictions(final String input) throws IOException;

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
     * @throws IOException if the time zone of the starting location cannot be obtained.
     */
    void setDeparturePlaceId(final String departurePlaceId) throws IOException;

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
     * @throws DirectionsApiException if an error occurs while requesting the desired routes.
     */
    void startDirectionsAnalysis(TravelRequest travelRequest) throws DirectionsApiException;

    /**
     * Retrieves the main travel mode result.
     *
     * @return a {@link TravelModeResult} object representing the results from analyzing the first route recommended by the routing service
     * @throws WeatherDataException if there is an error getting weather information for route analysis.
     */
    TravelModeResult getTravelModeMainResult() throws WeatherDataException;

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
     * @throws WeatherDataException  if there is an error getting weather information for route analysis.
     */
    Optional<List<TravelModeResult>> getAlternativesResults() throws WeatherDataException;

    /**
     * Finalizes and builds the {@link TravelRequest} with all configured parameters.
     *
     * @return the completed {@link TravelRequest} object.
     * @throws TravelRequestException if not all the parameters needed to calculate the routes have been entered.
     */
    TravelRequest finalizeTheRequest() throws TravelRequestException;
}
