package org.app.travelmode.model.google.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.app.travelmode.model.google.api.GoogleApiClientFactory;
import org.app.travelmode.model.google.api.StaticMapApiClient;

import java.io.FileReader;

/**
 * Implementation of {@link GoogleApiClientFactory} that creates various Google API clients
 * using a shared API key.
 */
public class GoogleApiClientFactoryImpl implements GoogleApiClientFactory {

    private String googleApiKey = null;

    /**
     * Constructs a new GoogleApiClientFactoryImpl and loads the API key.
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectionApiClientImpl createDirectionApiClient() {
        return new DirectionApiClientImpl(this.googleApiKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaceDetailsApiClient createPlaceDetailsApiClient() {
        return new PlaceDetailsApiClient(this.googleApiKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlacePredictionsApiClient createPlacePredictionsApiClient() {
        return new PlacePredictionsApiClient(this.googleApiKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StaticMapApiClient createStaticMapApiClient() {
        return new StaticMapApiClientImpl(this.googleApiKey);
    }
}
