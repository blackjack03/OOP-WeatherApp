package org.app.travelmode.model;

/**
 * Base interface for all clients that interact with Google APIs.
 * Provides essential methods for accessing basic configurations needed
 * to make Google API calls.
 */
public interface GoogleApiClient {

    /**
     * Returns the base URL for API calls.
     * This URL is used as a starting point for creating specific API requests.
     *
     * @return a string representing the API's base URL
     */
    String getBaseUrl();

    /**
     * Returns the API key required to authenticate requests
     * to Google services.
     *
     * @return a string representing the Google API key.
     */
    String getApiKey();

}
