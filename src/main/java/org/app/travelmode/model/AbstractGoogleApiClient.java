package org.app.travelmode.model;

import com.google.gson.Gson;
import org.app.model.AdvancedJsonReader;
import org.app.model.AdvancedJsonReaderImpl;

import java.util.Objects;

public abstract class AbstractGoogleApiClient implements GoogleApiClient {

    private final String baseUrl;
    private final String apiKey;

    public AbstractGoogleApiClient(final String baseUrl, final String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    protected <T> T executeRequest(final String requestUrl, final Class<T> responseClass) {
        final AdvancedJsonReader jsonReader = new AdvancedJsonReaderImpl();
        T response = null;
        try {
            jsonReader.requestJSON(requestUrl);
            final String rawJSon = jsonReader.getRawJSON();
            System.out.println(rawJSon); //TODO: da eliminare

            final Gson gson = new Gson();
            response = gson.fromJson(rawJSon, responseClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(response, "La chiamata all'api ha dato una risposta vuota");
        return response;
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
