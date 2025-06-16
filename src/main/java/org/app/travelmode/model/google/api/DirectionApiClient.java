package org.app.travelmode.model.google.api;

import org.app.travelmode.model.google.dto.directions.DirectionsResponse;
import org.app.travelmode.model.travel.api.TravelRequest;

public interface DirectionApiClient {

    /**
     * Retrieves directions between two locations based on the provided travel request.
     *
     * @param travelRequest the request containing origin, destination, and timing information
     * @return a {@link DirectionsResponse} object containing the route information
     */
    DirectionsResponse getDirections(TravelRequest travelRequest);
}
