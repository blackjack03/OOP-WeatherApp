package org.app.travelmode.model.analysis.impl;

import org.app.common.api.weather.WeatherDataProvider;
import org.app.travelmode.model.analysis.api.WeatherInformationService;
import org.app.travelmode.model.checkpoint.api.Checkpoint;
import org.app.travelmode.model.checkpoint.api.CheckpointWithMeteo;
import org.app.travelmode.model.checkpoint.impl.CheckpointWithMeteoImpl;
import org.app.travelmode.model.exception.WeatherDataException;
import org.app.travelmode.model.weather.api.WeatherCondition;
import org.app.travelmode.model.weather.api.WeatherConditionFactory;
import org.app.travelmode.model.weather.api.WeatherReport;
import org.app.travelmode.model.weather.impl.WeatherReportImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link WeatherInformationService} that enriches checkpoints with weather information.
 * This service retrieves weather data and creates detailed weather reports for specific locations and times.
 */
public class WeatherInformationServiceImpl implements WeatherInformationService {

    private final WeatherConditionFactory weatherConditionFactory;
    private final WeatherDataProvider weatherDataProvider;

    /**
     * Constructs a new {@link WeatherInformationServiceImpl} with the specified factory.
     *
     * @param weatherConditionFactory factory for creating different types of weather conditions
     * @param weatherDataProvider     data provider for retrieving weather data
     */
    public WeatherInformationServiceImpl(final WeatherConditionFactory weatherConditionFactory,
                                         final WeatherDataProvider weatherDataProvider) {
        this.weatherConditionFactory = weatherConditionFactory;
        this.weatherDataProvider = weatherDataProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckpointWithMeteo enrichWithWeather(final Checkpoint checkpoint) throws WeatherDataException {
        try {
            final Map<String, Number> weatherInformation = getWeatherInfo(checkpoint);
            final WeatherReport weatherReport = createWeatherReport(weatherInformation);
            return new CheckpointWithMeteoImpl(checkpoint, weatherReport);
        } catch (final IllegalArgumentException e) {
            throw new WeatherDataException(
                    String.format("Non è stato possibile ottenere tutte le informazioni meteo necessarie "
                                    + "per il checkpoint %f, %f, o alcune di esse non sono realistiche.",
                            +checkpoint.getLatitude(), checkpoint.getLongitude()), e);
        }
    }

    /**
     * Retrieves weather information for a specific checkpoint and time.
     *
     * @param checkpoint the checkpoint to get weather information for
     * @return a map containing various weather measurements
     * @throws WeatherDataException if it isn't possible to get weather information for the provided {@link Checkpoint}.
     */
    private Map<String, Number> getWeatherInfo(final Checkpoint checkpoint) throws WeatherDataException {
        final LocalDateTime arrivalTime = checkpoint.getArrivalDateTime().toLocalDateTime();
        return this.weatherDataProvider.getWeatherInfo(checkpoint.getLatitude(), checkpoint.getLongitude(), arrivalTime)
                .orElseThrow(() -> new WeatherDataException("Impossibile ottenere le informazioni meteo per il checkpoint: "
                        + checkpoint.getLatitude() + "," + checkpoint.getLongitude()));
    }

    /**
     * Creates a weather report from raw weather information.
     *
     * @param weatherInformation map containing raw weather measurements
     * @return a {@link WeatherReport} containing processed weather conditions
     * @throws IllegalArgumentException if one or more required weather conditions are missing
     *                                  or if some of them are unrealistic.
     */
    private WeatherReport createWeatherReport(final Map<String, Number> weatherInformation) {
        final List<WeatherCondition> weatherConditions = Arrays.asList(
                this.weatherConditionFactory.createFreezingRisk(weatherInformation.get("freezing_level_height").doubleValue()),
                this.weatherConditionFactory.createSnowfall(weatherInformation.get("snowfall").doubleValue()),
                this.weatherConditionFactory.createPrecipitation(weatherInformation.get("precipitation").doubleValue()),
                this.weatherConditionFactory.createVisibility(weatherInformation.get("visibility").doubleValue()),
                this.weatherConditionFactory.createWindGust(weatherInformation.get("wind_gusts").doubleValue())
        );
        return new WeatherReportImpl(weatherConditions);
    }
}
