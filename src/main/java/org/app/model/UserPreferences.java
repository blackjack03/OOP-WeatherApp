package org.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPreferences {
    private String defaultCity;
    private String units;
    private String language;

    @JsonProperty
    public String getDefaultCity() {
        return defaultCity;
    }

    @JsonProperty
    public void setDefaultCity(String defaultCity) {
        this.defaultCity = defaultCity;
    }

    @JsonProperty
    public String getUnits() {
        return units;
    }

    @JsonProperty
    public void setUnits(String units) {
        this.units = units;
    }

    @JsonProperty
    public String getLanguage() {
        return language;
    }

    @JsonProperty
    public void setLanguage(String language) {
        this.language = language;
    }
}