package org.app.travelmode.model;

import org.app.travelmode.model.google.dto.directions.DirectionsResponse;
import org.app.travelmode.model.travel.api.TravelModeResult;
import org.app.travelmode.model.travel.api.TravelRequest;

import java.util.List;

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
     * <p>
     * Throws an exception if no travel request is set.
     */
    void askForDirections();

    /**
     * Requests directions using the specified travel request.
     *
     * @param travelRequest the {@link TravelRequest} to use for the API request.
     */
    void askForDirections(TravelRequest travelRequest);

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
     */
    TravelModeResult getMainResult();

    /**
     * Returns a list of alternative route results after analyzing the API response.
     * If the results are not already computed, they are calculated and stored.
     *
     * @return a {@link List} of {@link TravelModeResult} for alternative routes.
     */
    List<TravelModeResult> getAlternativeResults();

    /**
     * Returns the raw API response as a {@link DirectionsResponse}.
     *
     * @return the {@link DirectionsResponse} object.
     */
    DirectionsResponse getDirectionsResponse();
}
