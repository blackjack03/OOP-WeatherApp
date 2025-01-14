package org.app.model;

public class UserPreferences {
    private String defaultCuty;
    private String units;
    private String language;

    public String getDefaultCuty() {
        return defaultCuty;
    }

    public void setDefaultCuty(String defaultCuty) {
        this.defaultCuty = defaultCuty;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}