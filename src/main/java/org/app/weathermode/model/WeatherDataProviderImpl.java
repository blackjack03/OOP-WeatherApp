package org.app.weathermode.model;

import org.app.common.api.weather.WeatherDataProvider;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * Adapter for the AllWeather component to expose it via the shared WeatherDataProvider interface.
 */
public class WeatherDataProviderImpl implements WeatherDataProvider {

    /**
     * Constructs a new Weather data provider implementation.
     */
    public WeatherDataProviderImpl() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Map<String, Number>> getWeatherInfo(double lat, double lng, final LocalDateTime dateTime) {
        final Map<String, String> coordinates = Map.of(
                "lat", String.valueOf(lat),
                "lng", String.valueOf(lng)
        );
        final Weather allWeather = new AllWeather(coordinates);
        final String time = String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
        return allWeather.getWeatherOn(
                dateTime.getDayOfMonth(),
                dateTime.getMonthValue(),
                dateTime.getYear(),
                time);
    }
}
