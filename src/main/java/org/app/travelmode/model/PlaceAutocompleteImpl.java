package org.app.travelmode.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.app.model.AdvancedJsonReader;
import org.app.travelmode.placeautocomplete.PlaceAutocompletePrediction;
import org.app.travelmode.placeautocomplete.PlaceAutocompleteResponse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlaceAutocompleteImpl implements PlaceAutocomplete {

    private String googleApiKey = null;

    public PlaceAutocompleteImpl() {
        try (FileReader jsonReader = new FileReader("src/main/resources/API-Keys.json")) {
            final Gson gson = new Gson();
            final JsonObject jsonObject = gson.fromJson(jsonReader, JsonObject.class);
            this.googleApiKey = jsonObject.get("google-api-key").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<PlaceAutocompletePrediction> getPlacePredictions(final String input) {
        final List<PlaceAutocompletePrediction> suggestions = new ArrayList<>();

        final String urlString = "https://maps.googleapis.com/maps/api/place/autocomplete/json" + "?input=" + input +
                "&language=it" +
                "&types=geocode" +
                "&key=" + googleApiKey;

        System.out.println(urlString);

        try {
            final URL url = new URL(urlString);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            final BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder results = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                results.append(line);
            }
            br.close();
            System.out.println(results.toString());
            final Gson gson = new Gson();
            final PlaceAutocompleteResponse placeAutocompleteResponse = gson.fromJson(results.toString(), PlaceAutocompleteResponse.class);
            System.out.println(placeAutocompleteResponse);
            suggestions.addAll(placeAutocompleteResponse.getPredictions());
            return suggestions;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
