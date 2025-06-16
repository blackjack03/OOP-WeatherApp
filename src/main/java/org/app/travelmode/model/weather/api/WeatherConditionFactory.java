package org.app.travelmode.model.weather;

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
     */
    FreezingRisk createFreezingRisk(double freezingHeightMetres);

    /**
     * Creates a precipitation condition based on the amount of rainfall.
     *
     * @param precipitationMm the amount of precipitation in millimeters
     * @return a new {@link Precipitation} instance
     */
    Precipitation createPrecipitation(double precipitationMm);

    /**
     * Creates a snowfall condition based on the amount of snow from the preceding hour.
     *
     * @param snowfallCm the amount of snowfall in centimeters from the preceding hour
     * @return a new {@link Snowfall} instance
     */
    Snowfall createSnowfall(double snowfallCm);

    /**
     * Creates a visibility condition based on the viewing distance.
     *
     * @param visibilityMeter the viewing distance in meters
     * @return a new {@link Visibility} instance
     */
    Visibility createVisibility(double visibilityMeter);

    /**
     * Creates a wind gust condition based on the maximum gust speed from the preceding hour.
     *
     * @param windGustSpeed the maximum wind gust speed in kilometers per hour measured at 10 m above ground
     * @return a new {@link WindGust} instance
     */
    WindGust createWindGust(double windGustSpeed);

}
