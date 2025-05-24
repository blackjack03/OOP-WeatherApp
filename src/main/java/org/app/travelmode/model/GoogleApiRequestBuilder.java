package org.app.travelmode.model;

public interface GoogleApiRequestBuilder {

    GoogleApiRequestBuilder addParameter(final String key, final String value);

    GoogleApiRequestBuilder reset();

    String build();
}
