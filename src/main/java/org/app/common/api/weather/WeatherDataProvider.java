package org.app.common.api.weather;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Provides weather data based on location and time.
 */
public interface WeatherDataProvider {
    /**
     * Retrieves weather data for the specified coordinates and time.
     *
     * @param lat      the latitude of the location
     * @param lng      the longitude of the location
     * @param dateTime the time for which weather is requested
     * @return an {@link Optional} containing the weather data, or empty if unavailable
     */
    Optional<Map<String, Number>> getWeatherInfo(double lat, double lng, LocalDateTime dateTime);
}

