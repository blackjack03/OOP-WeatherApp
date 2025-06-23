package org.app.travelmode.model.google.impl;

import org.app.model.AdvancedJsonReader;
import org.app.model.AdvancedJsonReaderImpl;
import org.app.travelmode.model.google.api.GoogleApiClient;

import java.io.IOException;

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
     * Makes an HTTP request to fetch JSON data from a specified URL.
     *
     * <p>This method handles the communication with external APIs by:
     * <ul>
     *     <li>Establishing a connection to the provided URL</li>
     *     <li>Reading the JSON response using an {@link AdvancedJsonReader}</li>
     *     <li>Processing and returning the raw JSON data</li>
     * </ul>
     *
     * @param requestUrl the complete URL to which the request should be made.
     *                   This should include any necessary query parameters
     * @return the raw JSON response as a {@link String}
     * @throws IOException if there's an error during the HTTP request or while reading
     *                     the response
     */
    protected String requestJson(final String requestUrl) throws IOException {
        final AdvancedJsonReader jsonReader = new AdvancedJsonReaderImpl();
        jsonReader.requestJSON(requestUrl);
        final String rawJSon = jsonReader.getRawJSON();
        System.out.println(rawJSon); //TODO: da eliminare
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
