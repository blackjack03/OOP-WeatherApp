package org.app.travelmode.model;

import org.app.travelmode.directions.DirectionsResponse;

public class DirectionApiClient extends AbstractGoogleApiClient {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json";

    public DirectionApiClient(final String apiKey) {
        super(BASE_URL, apiKey);
    }

    public DirectionsResponse getDirections(final TravelRequest travelRequest) {
        final GoogleApiRequestBuilder requestBuilder = new GoogleApiRequestBuilderImpl(this.getBaseUrl(), this.getApiKey());
        final String url = requestBuilder.addParameter("destination", "place_id:" + travelRequest.getArrivalLocationPlaceId())
                .addParameter("origin", "place_id:" + travelRequest.getDepartureLocationPlaceId())
                .addParameter("departure_time", String.valueOf(travelRequest.getDepartureDateTime().toEpochSecond()))
                .addParameter("alternatives", "true")
                .addParameter("language", "it")
                .addParameter("units", "metric")
                .build();
        return this.executeRequest(url, DirectionsResponse.class);
    }

}