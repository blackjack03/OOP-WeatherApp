package org.app.model;

import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPreferences {
    private Integer defaultCity;

    @JsonProperty
    public Optional<Integer> getDefaultCity() {
        return (this.defaultCity != null) ?
            Optional.of(defaultCity) : Optional.empty();
    }

    @JsonProperty
    public void setDefaultCity(final Integer defaultCity) {
        this.defaultCity = defaultCity;
    }

}
