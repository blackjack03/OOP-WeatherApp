package org.app.travelmode.model.google.impl;

import org.app.model.AdvancedJsonReader;
import org.app.model.AdvancedJsonReaderImpl;
import org.app.travelmode.model.google.api.GoogleApiClient;

/**
 * Abstract base class for Google API clients that provides common functionality
 * for making HTTP requests to Google APIs.
 *
 * <p>Concrete implementations should extend this class to provide specific
 * API functionality for different Google services.
 */
public abstract class AbstractGoogleApiClient implements GoogleApiClient {

    private final String baseUrl;
    private final String apiKey;

    /**
     * Constructs a new Google API client with the specified base URL and API key.
     *
     * @param baseUrl the base URL for the Google API endpoint
     * @param apiKey  the API key for authentication
     */
    public AbstractGoogleApiClient(final String baseUrl, final String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    /**
     * Makes a JSON request to the specified URL and returns the raw JSON response.
     * It uses an {@link AdvancedJsonReader} to process the response.
     *
     * @param requestUrl the complete URL to make the request
     * @return the raw JSON response as a {@link String}, or null if the request fails
     */
    protected String requestJson(final String requestUrl) {
        final AdvancedJsonReader jsonReader = new AdvancedJsonReaderImpl();
        String rawJSon = null;
        try {
            jsonReader.requestJSON(requestUrl);
            rawJSon = jsonReader.getRawJSON();
            System.out.println(rawJSon); //TODO: da eliminare
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rawJSon;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBaseUrl() {
        return this.baseUrl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getApiKey() {
        return this.apiKey;
    }
}
