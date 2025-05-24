package org.app.travelmode.model;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GoogleApiRequestBuilderImpl implements GoogleApiRequestBuilder {

    private final StringBuilder urlBuilder;
    private final String apiKey;
    private final String baseUrl;
    private boolean hasParameters;

    public GoogleApiRequestBuilderImpl(final String baseUrl, final String apiKey) {
        this.urlBuilder = new StringBuilder(baseUrl);
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.hasParameters = baseUrl.contains("?");
    }

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

    @Override
    public GoogleApiRequestBuilder reset() {
        this.urlBuilder.setLength(0);
        this.urlBuilder.append(this.baseUrl);
        this.hasParameters = baseUrl.contains("?");
        return this;
    }

    @Override
    public String build() {
        this.addParameter("key", this.apiKey);
        return this.urlBuilder.toString();
    }
}
