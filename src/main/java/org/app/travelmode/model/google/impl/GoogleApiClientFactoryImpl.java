package org.app.travelmode.model.google.impl;

import org.app.travelmode.model.google.api.GoogleApiClientFactory;
import org.app.travelmode.model.google.api.StaticMapApiClient;
import org.app.config.ApiConfig;
import org.app.config.ConfigManager;

/**
 * Implementation of {@link GoogleApiClientFactory} that creates various Google API clients
 * using a shared API key.
 */
public class GoogleApiClientFactoryImpl implements GoogleApiClientFactory {

    private final ApiConfig googleApiConfig;

    /**
     * Constructs a new GoogleApiClientFactoryImpl and loads the API key.
     *
     * @throws IllegalStateException if the configuration file has not been previously loaded
     *                               with the {@code ConfigManager.loadConfig} method.
     */
    public GoogleApiClientFactoryImpl() throws IllegalStateException {
        this.googleApiConfig = ConfigManager.getConfig().getApi();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectionApiClientImpl createDirectionApiClient() {
        return new DirectionApiClientImpl(this.getGoogleApiKey());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlaceDetailsApiClient createPlaceDetailsApiClient() {
        return new PlaceDetailsApiClient(this.getGoogleApiKey());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlacePredictionsApiClient createPlacePredictionsApiClient() {
        return new PlacePredictionsApiClient(this.getGoogleApiKey());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StaticMapApiClient createStaticMapApiClient() {
        return new StaticMapApiClientImpl(this.getGoogleApiKey());
    }

    /**
     * Retrieves the Google API key from the configuration.
     *
     * <p>This private method attempts to access the Google API key stored in the configuration.
     * If the API key is not present in the configuration, it throws an exception since the key
     * is required for all Google API operations.
     *
     * @return the Google API key as a String
     * @throws IllegalStateException if the API key is not found in the configuration
     */
    private String getGoogleApiKey() {
        if (this.googleApiConfig.getApiKey().isPresent()) {
            return this.googleApiConfig.getApiKey().get();
        } else {
            throw new IllegalStateException("La chiave API è richiesta ma non è stata trovata nella configurazione.");
        }
    }
}
