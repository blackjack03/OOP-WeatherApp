package org.app.travelmode.model;

import org.app.model.Weather;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WeatherInformationServiceImpl implements WeatherInformationService {
    private final WeatherConditionFactory weatherConditionFactory;

    public WeatherInformationServiceImpl(final WeatherConditionFactory weatherConditionFactory) {
        this.weatherConditionFactory = weatherConditionFactory;
    }

    public CheckpointWithMeteo enrichWithWeather(final Checkpoint checkpoint) {
        final Map<String, Number> weatherInformation = getWeatherInfo(checkpoint);
        final WeatherReport weatherReport = createWeatherReport(weatherInformation);
        return new CheckpointWithMeteoImpl(checkpoint, weatherReport);
    }

    private Map<String, String> createCoordinatesMap(final Checkpoint checkpoint) {
        return Map.of(
                "lat", String.valueOf(checkpoint.getLatitude()),
                "lng", String.valueOf(checkpoint.getLongitude())
        );
    }

    private Map<String, Number> getWeatherInfo(final Checkpoint checkpoint) {
        final Weather weather = new Weather(createCoordinatesMap(checkpoint));
        final ZonedDateTime arrivalDateTime = checkpoint.getArrivalDateTime();
        final String arrivalHour = String.format("%d:%d", arrivalDateTime.getHour(), arrivalDateTime.getMinute());

        return weather.getWeatherOn(
                arrivalDateTime.getDayOfMonth(),
                arrivalDateTime.getMonthValue(),
                arrivalDateTime.getYear(),
                arrivalHour).orElseThrow(() -> new IllegalStateException("Impossibile ottenere le informazioni meteo"));
    }

    private WeatherReport createWeatherReport(final Map<String, Number> weatherInformation) {
        final WeatherReport weatherReport = new WeatherReportImpl();
        final List<WeatherCondition> weatherConditions = Arrays.asList(
                this.weatherConditionFactory.createFreezingRisk(weatherInformation.get("freezing_level_height").doubleValue()),
                this.weatherConditionFactory.createSnowfall(weatherInformation.get("snowfall").doubleValue()),
                this.weatherConditionFactory.createPrecipitation(weatherInformation.get("precipitation").doubleValue()),
                this.weatherConditionFactory.createVisibility(weatherInformation.get("visibility").doubleValue()),
                this.weatherConditionFactory.createWindGust(weatherInformation.get("wind_gusts").doubleValue())
        );
        weatherReport.addConditions(weatherConditions);
        return weatherReport;
    }
}
