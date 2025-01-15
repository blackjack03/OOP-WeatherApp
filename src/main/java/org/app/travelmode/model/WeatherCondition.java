package org.app.travelmode.model;

public interface WeatherCondition {

    /**
     * Calculates and returns the impact of the weather condition.
     * The impact is determined by multiplying the base impact of the weather type
     * by the multiplier associated with the intensity.
     *
     * @return a double value representing the impact of the weather condition.
     */
    double getImpact();

    /**
     * Returns the type of weather.
     *
     * @return a {@link WeatherType} representing the type of this weather condition.
     */
    WeatherType getWeatherType();

    /**
     * Returns the intensity of the weather.
     *
     * @return an {@link Intensity} representing the intensity of the weather condition.
     */
    Intensity getIntensity();
}
