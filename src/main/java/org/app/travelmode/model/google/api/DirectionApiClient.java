package org.app.travelmode.model.google.api;

import org.app.travelmode.model.exception.DirectionsApiException;
import org.app.travelmode.model.google.dto.directions.DirectionsResponse;
import org.app.travelmode.model.travel.api.TravelRequest;

/**
 * {@code DirectionApiClient} defines the contract for any client that connects to a directions API
 * (for example, Google Directions API or similar services).
 * <p>
 * It is responsible for sending a travel request and receiving a route with details like steps, duration, and distance.
 *
 */
public interface DirectionApiClient {

    /**
     * Retrieves directions between two locations based on the provided travel request.
     *
     * @param travelRequest the request containing origin, destination, and timing information
     * @return a {@link DirectionsResponse} object containing the route information
     * @throws DirectionsApiException if:
     *                                <ul>
     *                                   <li>The API returns a null response</li>
     *                                    <li>The API returns a status other than "OK"</li>
     *                                    <li>There's an error during the HTTP request</li>
     *                                </ul>
     */
    DirectionsResponse getDirections(TravelRequest travelRequest) throws DirectionsApiException;
}
