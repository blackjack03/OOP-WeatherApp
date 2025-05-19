package org.app.travelmode.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileReader;

public class GoogleApiClientFactoryImpl implements GoogleApiClientFactory {

    private String googleApiKey = null;

    public GoogleApiClientFactoryImpl() {
        //TODO: integrare in json reader
        try (FileReader jsonReader = new FileReader("src/main/resources/API-Keys.json")) {
            final Gson gson = new Gson();
            final JsonObject jsonObject = gson.fromJson(jsonReader, JsonObject.class);
            this.googleApiKey = jsonObject.get("google-api-key").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public DirectionApiClient createDirectionApiClient() {
        return new DirectionApiClient(this.googleApiKey);
    }

    @Override
    public PlaceDetailsApiClient createPlaceDetailsApiClient() {
        return new PlaceDetailsApiClient(this.googleApiKey);
    }

    @Override
    public PlacePredictionsApiClient createPlacePredictionsApiClient() {
        return new PlacePredictionsApiClient(this.googleApiKey);
    }
}
