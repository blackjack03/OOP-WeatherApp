package org.app.travelmode.model.weather.impl;

import org.app.travelmode.model.weather.api.WeatherConditionFactory;
import org.app.travelmode.model.weather.impl.conditions.*;

/**
 * Default implementation of the {@link WeatherConditionFactory} interface.
 * This class provides concrete implementations for creating various weather condition objects.
 */
public class WeatherConditionFactoryImpl implements WeatherConditionFactory {

    private static final double MIN_WIND_SPEED = 0.0;
    private static final double MIN_VISIBILITY_METER = 0.0;
    private static final double MIN_FREEZING_HEIGHT_METRES = -500.0;
    private static final double MIN_PRECIPITATION_MM = 0.0;
    private static final double MIN_SNOWFALL_CM = 0.0;

    /**
     * Constructs a new {@link WeatherConditionFactoryImpl} instance.
     */
    public WeatherConditionFactoryImpl() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FreezingRisk createFreezingRisk(double freezingHeightMetres) {
        this.validateRange("Altitudine di congelamento", freezingHeightMetres, MIN_FREEZING_HEIGHT_METRES);
        return new FreezingRisk(freezingHeightMetres);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Precipitation createPrecipitation(double precipitationMm) {
        this.validateRange("Quantità di precipitazioni", precipitationMm, MIN_PRECIPITATION_MM);
        return new Precipitation(precipitationMm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Snowfall createSnowfall(double snowfallCm) {
        this.validateRange("Quantità di neve", snowfallCm, MIN_SNOWFALL_CM);
        return new Snowfall(snowfallCm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Visibility createVisibility(double visibilityMeter) {
        this.validateRange("Visibilità", visibilityMeter, MIN_VISIBILITY_METER);
        return new Visibility(visibilityMeter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WindGust createWindGust(double windGustSpeed) {
        this.validateRange("Velocità del vento", windGustSpeed, MIN_WIND_SPEED);
        return new WindGust(windGustSpeed);
    }

    /**
     * Verify that the parameters provided are realistic
     *
     * @param paramName the name of the parameter being validated
     * @param value     the value to validate
     * @param min       the minimum acceptable value
     */
    private void validateRange(final String paramName, double value, double min) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException(String.format("%s non può essere NaN", paramName));
        }
        if (Double.isInfinite(value)) {
            throw new IllegalArgumentException(String.format("%s non può essere infinito", paramName));
        }
        if (value < min) {
            throw new IllegalArgumentException(String.format("%s non può essere minore di %.1f", paramName, min));
        }
    }

}
