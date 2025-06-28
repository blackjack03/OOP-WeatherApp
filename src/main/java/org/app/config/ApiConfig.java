package org.app.config;

import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiConfig {
    private String apiKey;

    @JsonProperty
    public Optional<String> getApiKey() {
        return apiKey == null ? Optional.empty() : Optional.of(apiKey);
    }

    @JsonProperty
    public void setApiKey(final String apiKey) {
        this.apiKey = apiKey;
    }

}
