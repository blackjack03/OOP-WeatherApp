package org.app.travelmode.model.google.api;

import org.app.travelmode.model.exception.DirectionsApiException;
import org.app.travelmode.model.google.dto.directions.DirectionsResponse;
import org.app.travelmode.model.travel.api.TravelRequest;

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
