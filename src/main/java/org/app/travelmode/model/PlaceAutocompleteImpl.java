package org.app.travelmode.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.app.model.AdvancedJsonReader;
import org.app.model.AdvancedJsonReaderImpl;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;
import org.app.travelmode.placeautocomplete.PlaceAutocompleteResponse;

import java.io.FileReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PlaceAutocompleteImpl implements PlaceAutocomplete {

    private String googleApiKey = null;

    /*
     * TODO: migliorare il meccanismo di ottenimento della api key
     * */
    public PlaceAutocompleteImpl() {
        try (FileReader jsonReader = new FileReader("src/main/resources/API-Keys.json")) {
            final Gson gson = new Gson();
            final JsonObject jsonObject = gson.fromJson(jsonReader, JsonObject.class);
            this.googleApiKey = jsonObject.get("google-api-key").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*TODO: migliorare la gestione di possibili errori ed exception. Controllare lo status della risposta dell'api...
     */
    @Override
    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) {
        final AdvancedJsonReader jsonReader = new AdvancedJsonReaderImpl();

        try {
            final String encodedInput = URLEncoder.encode(input, StandardCharsets.UTF_8);
            final String urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json" + "?input=" + encodedInput +
                    "&language=it" +
                    "&types=geocode" +
                    "&location=41.9028,12.4964" +
                    "&radius=500000" +
                    "&key=" + googleApiKey;

            jsonReader.requestJSON(urlString);
            final String rawJson = jsonReader.getRawJSON();

            final Gson gson = new Gson();
            final PlaceAutocompleteResponse placeAutocompleteResponse = gson.fromJson(rawJson, PlaceAutocompleteResponse.class);
            return List.copyOf(placeAutocompleteResponse.getPredictions());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
