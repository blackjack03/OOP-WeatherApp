package org.app.travelmode.model.weather.api;

import org.app.travelmode.model.weather.impl.conditions.*;

/**
 * Factory interface for creating various weather condition objects.
 * This interface provides methods to instantiate different types of weather conditions
 * with their respective measurement values.
 */
public interface WeatherConditionFactory {

    /**
     * Creates a freezing risk condition based on the 0°C level altitude.
     *
     * @param freezingHeightMetres the altitude above sea level where 0°C (freezing level) is reached.
     * @return a new {@link FreezingRisk} instance
     * @throws IllegalArgumentException if the freezing height is less than -500.0 meters,
     *                                  or if the value is NaN or infinite
     */
    FreezingRisk createFreezingRisk(double freezingHeightMetres);

    /**
     * Creates a precipitation condition based on the amount of rainfall.
     *
     * @param precipitationMm the amount of precipitation in millimeters
     * @return a new {@link Precipitation} instance
     * @throws IllegalArgumentException if the precipitation amount is negative,
     *                                  or if the value is NaN or infinite
     */
    Precipitation createPrecipitation(double precipitationMm);

    /**
     * Creates a snowfall condition based on the amount of snow from the preceding hour.
     *
     * @param snowfallCm the amount of snowfall in centimeters from the preceding hour
     * @return a new {@link Snowfall} instance
     * @throws IllegalArgumentException if the snowfall amount is negative,
     *                                  or if the value is NaN or infinite
     */
    Snowfall createSnowfall(double snowfallCm);

    /**
     * Creates a visibility condition based on the viewing distance.
     *
     * @param visibilityMeter the viewing distance in meters
     * @return a new {@link Visibility} instance
     * @throws IllegalArgumentException if the visibility is negative,
     *                                  or if the value is NaN or infinite
     */
    Visibility createVisibility(double visibilityMeter);

    /**
     * Creates a wind gust condition based on the maximum gust speed from the preceding hour.
     *
     * @param windGustSpeed the maximum wind gust speed in kilometers per hour measured at 10 m above ground
     * @return a new {@link WindGust} instance
     * @throws IllegalArgumentException if the wind speed is negative,
     *                                  or if the value is NaN or infinite
     */
    WindGust createWindGust(double windGustSpeed);

}
