package org.app.travelmode.model;

import com.google.gson.Gson;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;
import org.app.travelmode.placeautocomplete.PlaceAutocompleteResponse;

import java.util.List;

public class PlacePredictionsApiClient extends AbstractGoogleApiClient {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json";

    public PlacePredictionsApiClient(final String apiKey) {
        super(BASE_URL, apiKey);
    }

    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) {
        final GoogleApiRequestBuilder requestBuilder = new GoogleApiRequestBuilderImpl(BASE_URL, this.getApiKey());
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
