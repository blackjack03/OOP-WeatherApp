package org.app.travelmode.model;

import com.google.gson.Gson;
import org.app.travelmode.directions.DirectionsResponse;

import java.util.Objects;

public class DirectionApiClient extends AbstractGoogleApiClient {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json";

    public DirectionApiClient(final String apiKey) {
        super(BASE_URL, apiKey);
    }

    public DirectionsResponse getDirections(final TravelRequest travelRequest) {
        final GoogleApiRequestBuilder requestBuilder = new GoogleApiRequestBuilderImpl(this.getBaseUrl(), this.getApiKey());
        DirectionsResponse directionsResponse = null;
        final String url = requestBuilder.addParameter("destination", "place_id:" + travelRequest.getArrivalLocationPlaceId())
                .addParameter("origin", "place_id:" + travelRequest.getDepartureLocationPlaceId())
                .addParameter("departure_time", String.valueOf(travelRequest.getDepartureDateTime().toEpochSecond()))
                .addParameter("alternatives", "true")
                .addParameter("language", "it")
                .addParameter("units", "metric")
                .build();
        try {
            final Gson gson = new Gson();
            directionsResponse = gson.fromJson(this.requestJson(url), DirectionsResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(directionsResponse, "La chiamata all'api directions ha restituito un risultato nullo.");
        return directionsResponse;
    }

}