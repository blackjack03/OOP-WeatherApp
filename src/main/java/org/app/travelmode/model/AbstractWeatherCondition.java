package org.app.travelmode.model;

public abstract class AbstractWeatherCondition implements WeatherCondition {

    private static final int WORST_INTENSITY_SCORE = 100;

    private final String conditionName;
    private final double weight;

    public AbstractWeatherCondition(final String conditionName, double weight) {
        this.conditionName = conditionName;
        this.weight = weight;
    }

    @Override
    public String getConditionName() {
        return this.conditionName;
    }

    @Override
    public double getWeightedIntensityScore() {
        return getIntensityScore() * weight;
    }

    @Override
    public double getWorstWeightedIntensityScore() {
        return WORST_INTENSITY_SCORE * weight;
    }

    @Override
    public abstract int getIntensityScore();
}
