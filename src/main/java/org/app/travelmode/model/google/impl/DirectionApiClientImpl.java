package org.app.travelmode.model.google.impl;

import com.google.gson.Gson;
import org.app.travelmode.model.exception.DirectionsApiException;
import org.app.travelmode.model.google.dto.directions.DirectionsResponse;
import org.app.travelmode.model.travel.api.TravelRequest;
import org.app.travelmode.model.google.api.DirectionApiClient;
import org.app.travelmode.model.google.api.GoogleApiRequestBuilder;

import java.io.IOException;

/**
 * A client for interacting with Google Directions API.
 * Extends {@link AbstractGoogleApiClient} to provide specific functionality
 * for retrieving route directions between locations.
 */
public class DirectionApiClientImpl extends AbstractGoogleApiClient implements DirectionApiClient {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json";

    /**
     * Constructs a new DirectionApiClientImpl with the specified API key.
     *
     * @param apiKey the Google API key to authenticate requests.
     */
    public DirectionApiClientImpl(final String apiKey) {
        super(BASE_URL, apiKey);
    }

    /**
     * {@inheritDoc}
     *
     * <p>The method performs the following:
     * <ul>
     *     <li>Builds a request URL with all necessary parameters</li>
     *     <li>Executes the request to the Google Directions API</li>
     *     <li>Parses the JSON response into a DirectionsResponse object</li>
     * </ul>
     *
     * @param travelRequest a {@link TravelRequest} object containing origin, destination,
     *                      and departure time details
     * @return a {@link DirectionsResponse} object containing the route information
     * @throws DirectionsApiException if:
     *                                <ul>
     *                                   <li>The API returns a null response</li>
     *                                    <li>The API returns a status other than "OK"</li>
     *                                    <li>There's an error during the HTTP request</li>
     *                                </ul>
     */
    @Override
    public DirectionsResponse getDirections(final TravelRequest travelRequest) throws DirectionsApiException {
        try {
            final GoogleApiRequestBuilder requestBuilder = new GoogleApiRequestBuilderImpl(this.getBaseUrl(), this.getApiKey());
            final String url = requestBuilder.addParameter("destination", "place_id:" + travelRequest.getArrivalLocationPlaceId())
                    .addParameter("origin", "place_id:" + travelRequest.getDepartureLocationPlaceId())
                    .addParameter("departure_time", String.valueOf(travelRequest.getDepartureDateTime().toEpochSecond()))
                    .addParameter("alternatives", "true")
                    .addParameter("language", "it")
                    .addParameter("units", "metric")
                    .build();

            final Gson gson = new Gson();
            final DirectionsResponse directionsResponse = gson.fromJson(this.requestJson(url), DirectionsResponse.class);

            if (directionsResponse == null) {
                throw new DirectionsApiException("La chiamata all'API Directions ha restituito un risultato nullo.");
            }

            if (!directionsResponse.getStatus().equals("OK")) {
                throw new DirectionsApiException(String.format("Errore nella ricerca del percorso: %s\n%s",
                        directionsResponse.getStatus(), directionsResponse.getErrorMessage()));
            }

            return directionsResponse;
        } catch (final IOException e) {
            throw new DirectionsApiException("Errore durante la richiesta HTTP all'API Directions.", e);
        }
    }

}