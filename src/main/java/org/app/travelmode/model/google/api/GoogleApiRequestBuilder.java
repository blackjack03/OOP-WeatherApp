package org.app.travelmode.model.google.api;

/**
 * Builder interface for constructing Google API request URLs.
 */
public interface GoogleApiRequestBuilder {

    /**
     * Adds a parameter to the request URL.
     *
     * @param key   the parameter name
     * @param value the parameter value
     * @return this builder instance for method chaining
     */
    GoogleApiRequestBuilder addParameter(String key, String value);

    /**
     * Resets the builder to its initial state.
     * Removes all previously added parameters.
     *
     * @return this builder instance for method chaining
     */
    GoogleApiRequestBuilder reset();

    /**
     * Builds and returns the final URL string with all added parameters.
     * The URL will be properly formatted with all parameters correctly encoded.
     *
     * @return the complete URL string with all parameters
     */
    String build();
}
