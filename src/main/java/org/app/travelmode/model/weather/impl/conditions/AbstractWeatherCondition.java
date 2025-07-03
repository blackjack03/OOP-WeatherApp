package org.app.travelmode.model.weather.impl.conditions;

import org.app.travelmode.model.weather.api.WeatherCondition;

/**
 * Abstract base class for weather conditions that provides common functionality for all weather conditions.
 * This class implements the {@link WeatherCondition} interface and provides a template for creating
 * specific weather condition implementations.
 */
public abstract class AbstractWeatherCondition implements WeatherCondition {

    private static final int WORST_INTENSITY_SCORE = 100;

    private final String conditionName;
    private final double weight;

    /**
     * Constructs a new weather condition with the specified name and weight.
     *
     * @param conditionName the name of the weather condition
     * @param weight        the weight factor to be applied to this condition's intensity scores
     */
    public AbstractWeatherCondition(final String conditionName, final double weight) {
        this.conditionName = conditionName;
        this.weight = weight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConditionName() {
        return this.conditionName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getWeightedIntensityScore() {
        return getIntensityScore() * weight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getWorstWeightedIntensityScore() {
        return WORST_INTENSITY_SCORE * weight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int getIntensityScore();
}
