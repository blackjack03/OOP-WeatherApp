package org.app.travelmode.model.google.impl;

import org.app.travelmode.model.google.api.GoogleApiClientFactory;
import org.app.travelmode.model.google.api.StaticMapApiClient;
import org.app.weathermode.model.ApiConfig;
import org.app.weathermode.model.ConfigManager;

/**
 * Implementation of {@link GoogleApiClientFactory} that creates various Google API clients
 * using a shared API key.
 */
public class GoogleApiClientFactoryImpl implements GoogleApiClientFactory {

    private static final String CONFIG_PATH = "src/main/java/org/files/configuration.json";
    private String googleApiKey = null;

    /**
     * Constructs a new GoogleApiClientFactoryImpl and loads the API key.
     */
    public GoogleApiClientFactoryImpl() {
        ConfigManager.loadConfig(CONFIG_PATH);
        final ApiConfig APIConfig = ConfigManager.getConfig().getApi();
        if (APIConfig.getApiKey().isPresent()) {
            this.googleApiKey = APIConfig.getApiKey().get();
        } else {
            System.err.println("API key not found in configuration. Please set it in the configuration file.");
            throw new IllegalStateException("API key is required but not found in the configuration.");
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
