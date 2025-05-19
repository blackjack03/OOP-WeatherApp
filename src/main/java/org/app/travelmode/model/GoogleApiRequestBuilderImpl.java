package org.app.travelmode.model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GoogleApiRequestBuilderImpl implements GoogleApiRequestBuilder {

    private final StringBuilder urlBuilder;
    private final String apiKey;

    public GoogleApiRequestBuilderImpl(final String baseUrl, final String apiKey) {
        this.urlBuilder = new StringBuilder(baseUrl);
        this.apiKey = apiKey;
    }

    @Override
    public GoogleApiRequestBuilder addParameter(final String key, final String value) {
        char separator = this.urlBuilder.toString().contains("?") ? '&' : '?';
        this.urlBuilder.append(separator)
                .append(key)
                .append('=')
                .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
        return this;
    }

    @Override
    public String build() {
        this.addParameter("key", this.apiKey);
        return this.urlBuilder.toString();
    }
}
