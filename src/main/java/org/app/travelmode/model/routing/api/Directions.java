package org.app.travelmode.model.routing.api;

import org.app.travelmode.model.exception.DirectionsApiException;
import org.app.travelmode.model.exception.WeatherDataException;
import org.app.travelmode.model.google.dto.directions.DirectionsResponse;
import org.app.travelmode.model.travel.api.TravelModeResult;
import org.app.travelmode.model.travel.api.TravelRequest;

import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for handling route directions and their analysis.
 *
 * <p>This interface provides methods to:
 * <ul>
 *     <li>Configure and execute travel requests</li>
 *     <li>Retrieve and analyze route information</li>
 *     <li>Access alternative routes</li>
 *     <li>Manage raw API responses</li>
 * </ul>
 *
 * <p>The typical workflow involves:
 * <ol>
 *     <li>Setting or providing a travel request</li>
 *     <li>Requesting directions from the service</li>
 *     <li>Retrieving and analyzing the results</li>
 * </ol>
 */
public interface Directions {

    /**
     * Sets a new travel request and clears any previous results or responses.
     *
     * @param travelRequest the new {@link TravelRequest} to be set.
     */
    void setTravelRequest(TravelRequest travelRequest);

    /**
     * Requests directions based on the current travel request.
     *
     * <p>This method uses the previously set travel request to:
     * <ul>
     *      <li>Contact the directions service</li>
     *      <li>Retrieve route information</li>
     *      <li>Store the response for analysis</li>
     * </ul>
     *
     * @throws IllegalStateException  if no travel request has been set using
     *                                {@link #setTravelRequest(TravelRequest)}
     * @throws DirectionsApiException if there's an error while fetching directions
     *                                from the Google Directions API
     */
    void askForDirections() throws DirectionsApiException;

    /**
     * Requests directions using the provided travel request.
     *
     * <p>This method uses the provided travel request to:
     * <ul>
     *      <li>Contact the directions service</li>
     *      <li>Retrieve route information</li>
     *      <li>Store the response for analysis</li>
     * </ul>
     *
     * @param travelRequest the {@link TravelRequest} to use for the API request.
     * @throws DirectionsApiException if there's an error while fetching directions
     *                                from the Google Directions API
     */
    void askForDirections(TravelRequest travelRequest) throws DirectionsApiException;

    /**
     * Returns the main route result after analyzing the API response.
     * If the result is not already computed, it is calculated and stored.
     *
     * <p>The main result typically represents:
     * <ul>
     *     <li>The fastest route</li>
     *     <li>Detailed checkpoint information</li>
     *     <li>Associated weather conditions</li>
     * </ul>
     *
     * @return the {@link TravelModeResult} for the main route.
     * @throws IllegalStateException if called before obtaining directions via {@link #askForDirections()}
     *                               or {@link #askForDirections(TravelRequest)}
     *                               or if the DirectionsResponse is not present
     * @throws WeatherDataException  if there is an error getting weather information for route analysis.
     */
    TravelModeResult getMainResult() throws WeatherDataException;

    /**
     * Retrieves alternative routes for the current travel request if available.
     *
     * <p>This method analyzes all alternative routes provided by the Google Directions API,
     * excluding the main route (which is accessible via {@link #getMainResult()}).
     * Each alternative route is processed to include weather information and timing details.
     *
     * <p>The method caches the results after the first call. Subsequent calls will return
     * the cached results without reprocessing the routes.
     *
     * @return an {@link Optional} containing a {@link List} of {@link TravelModeResult}s representing
     * alternative routes. If no alternatives are available, returns an empty Optional.
     * @throws IllegalStateException if called before obtaining directions via {@link #askForDirections()}
     *                               or {@link #askForDirections(TravelRequest)}
     *                               or if the DirectionsResponse is not present
     * @throws WeatherDataException  if there is an error getting weather information for route analysis.
     */
    Optional<List<TravelModeResult>> getAlternativeResults() throws WeatherDataException;

    /**
     * Returns the raw API response as a {@link DirectionsResponse}.
     *
     * <p>The response becomes available only after a successful call to
     * {@link #askForDirections()} or {@link #askForDirections(TravelRequest)}.
     *
     * @return the {@link DirectionsResponse} containing the complete API response data.
     * @throws IllegalStateException if called before making a successful request
     *                               to the Directions API or if the response is not present
     */
    DirectionsResponse getDirectionsResponse();
}
