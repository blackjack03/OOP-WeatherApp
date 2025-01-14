package org.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPreferences {
    private String defaultCuty;
    private String units;
    private String language;

    @JsonProperty
    public String getDefaultCuty() {
        return defaultCuty;
    }

    @JsonProperty
    public void setDefaultCuty(String defaultCuty) {
        this.defaultCuty = defaultCuty;
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