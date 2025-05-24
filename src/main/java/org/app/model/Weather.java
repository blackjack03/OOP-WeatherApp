package org.app.model;

import java.util.*;

public interface Weather {

    void setLocation(Map<String, String> locationInfo);

    boolean reqestsAllForecast();

    Optional<Map<String, Map<String, Map<String, Number>>>> getAllForecast();

    Optional<Map<String, Map<String, Number>>> getDailyGeneralForecast();

    Optional<Map<String, Map<String, String>>> getDailyInfo();

    Optional<Map<String, Number>> getWeatherOn(int day, int month, int year, String hour);

    int getForecastDays();

    Optional<Pair<String, Map<String, Number>>> getWeatherNow();

    Optional<Map<String, Number>> getCityInfo();

    String getWindDirection(int degrees);

}
