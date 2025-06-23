package org.app.travelmode.model.google.impl;

import com.google.gson.Gson;
import org.app.travelmode.model.google.api.GoogleApiRequestBuilder;
import org.app.travelmode.model.google.api.PlaceAutocomplete;
import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompletePrediction;
import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompleteResponse;

import java.io.IOException;
import java.util.List;

/**
 * Client implementation for Google Places Autocomplete API that provides place predictions
 * based on user input.
 *
 * <p>This client:
 * <ul>
 *     <li>Provides location-based place predictions</li>
 *     <li>Uses Rome, Italy as the default location center (41.9028,12.4964)</li>
 *     <li>Prioritizes results within a radius of 500 km</li>
 *     <li>Returns predictions in Italian language</li>
 *     <li>Focuses on geocoding results</li>
 * </ul>
 */
public class PlacePredictionsApiClient extends AbstractGoogleApiClient implements PlaceAutocomplete {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json";

    private final GoogleApiRequestBuilder requestBuilder;

    /**
     * Constructs a new PlacePredictionsApiClient with the specified API key.
     *
     * @param apiKey the Google API key to use for requests
     */
    public PlacePredictionsApiClient(final String apiKey) {
        super(BASE_URL, apiKey);
        this.requestBuilder = new GoogleApiRequestBuilderImpl(BASE_URL, this.getApiKey());
    }

    /**
     * {@inheritDoc}
     *
     * @throws IOException if there's an error communicating with the Google Places API
     *                     or parsing the response
     */
    @Override
    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) throws IOException {
        this.requestBuilder.reset();
        final String url = requestBuilder.addParameter("input", input)
                .addParameter("language", "it")
                .addParameter("types", "geocode")
                .addParameter("location", "41.9028,12.4964")
                .addParameter("radius", "500000")
                .build();

        final Gson gson = new Gson();
        final PlaceAutocompleteResponse placeAutocompleteResponse =
                gson.fromJson(this.requestJson(url), PlaceAutocompleteResponse.class);

        return placeAutocompleteResponse != null ? List.copyOf(placeAutocompleteResponse.getPredictions()) : List.of();
    }
}
