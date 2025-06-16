package org.app.travelmode.model.google.impl;

import com.google.gson.Gson;
import org.app.travelmode.model.google.api.GoogleApiRequestBuilder;
import org.app.travelmode.model.google.api.PlaceAutocomplete;
import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompletePrediction;
import org.app.travelmode.model.google.dto.placeautocomplete.PlaceAutocompleteResponse;

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
     */
    @Override
    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) {
        this.requestBuilder.reset();
        PlaceAutocompleteResponse placeAutocompleteResponse = null;
        final String url = requestBuilder.addParameter("input", input)
                .addParameter("language", "it")
                .addParameter("types", "geocode")
                .addParameter("location", "41.9028,12.4964")
                .addParameter("radius", "500000")
                .build();
        try {
            final Gson gson = new Gson();
            placeAutocompleteResponse = gson.fromJson(this.requestJson(url), PlaceAutocompleteResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return placeAutocompleteResponse != null ? List.copyOf(placeAutocompleteResponse.getPredictions()) : List.of();
    }
}
