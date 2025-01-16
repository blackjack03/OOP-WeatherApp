package org.app.travelmode.model;

import org.app.travelmode.directions.DirectionsResponse;

import java.util.List;

public interface Directions {

    /**
     * Sets a new travel request and clears any previous results or responses.
     *
     * @param travelRequest the new {@link TravelRequest} to be set.
     */
    void setTravelRequest(TravelRequest travelRequest);

    /**
     * Requests directions based on the current travel request.
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
