package org.app.model;

public class AppConfig {
    private ApiConfig api;
    private UserPreferences userPreferences;

    public ApiConfig getApi() {
        return api;
    }

    public UserPreferences getUserPreferences() {
        return userPreferences;
    }
}