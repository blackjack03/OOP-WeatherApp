package org.app.travelmode.model;

import org.app.model.AdvancedJsonReader;
import org.app.model.AdvancedJsonReaderImpl;

public abstract class AbstractGoogleApiClient implements GoogleApiClient {

    private final String baseUrl;
    private final String apiKey;

    public AbstractGoogleApiClient(final String baseUrl, final String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

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

    @Override
    public String getBaseUrl() {
        return this.baseUrl;
    }

    @Override
    public String getApiKey() {
        return this.apiKey;
    }
}
