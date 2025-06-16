package org.app.travelmode.model.google;

import org.app.travelmode.model.google.api.GoogleApiRequestBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of {@link GoogleApiRequestBuilder} that constructs properly formatted URLs
 * for Google API requests.
 *
 * <p>This builder:
 * <ul>
 *     <li>Manages URL parameter concatenation</li>
 *     <li>Handles URL encoding of parameter values</li>
 *     <li>Automatically appends the API key</li>
 *     <li>Supports URL reset and reuse</li>
 * </ul>
 */
public class GoogleApiRequestBuilderImpl implements GoogleApiRequestBuilder {

    private final StringBuilder urlBuilder;
    private final String apiKey;
    private final String baseUrl;
    private boolean hasParameters;

    /**
     * Constructs a new {@link GoogleApiRequestBuilderImpl} with the specified base URL and API key.
     *
     * <p>The builder will automatically detect if the base URL already contains query parameters
     * and adjust its behavior accordingly.
     *
     * @param baseUrl the base URL for the API request.
     * @param apiKey  the Google API key to be appended to all requests.
     */
    public GoogleApiRequestBuilderImpl(final String baseUrl, final String apiKey) {
        this.urlBuilder = new StringBuilder(baseUrl);
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.hasParameters = baseUrl.contains("?");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoogleApiRequestBuilder addParameter(final String key, final String value) {
        if (this.hasParameters) {
            this.urlBuilder.append('&');
        } else {
            this.urlBuilder.append('?');
            this.hasParameters = true;
        }
        this.urlBuilder.append(key)
                .append('=')
                .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GoogleApiRequestBuilder reset() {
        this.urlBuilder.setLength(0);
        this.urlBuilder.append(this.baseUrl);
        this.hasParameters = baseUrl.contains("?");
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String build() {
        this.addParameter("key", this.apiKey);
        return this.urlBuilder.toString();
    }
}
