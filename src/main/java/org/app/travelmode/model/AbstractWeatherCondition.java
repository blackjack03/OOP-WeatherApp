package org.app.travelmode.model;

public abstract class AbstractWeatherCondition implements WeatherCondition {

    private final String conditionName;
    private final int intensityScore;

    public AbstractWeatherCondition(final String conditionName, int intensityScore) {
        this.conditionName = conditionName;
        this.intensityScore = intensityScore;
    }

    @Override
    public String getConditionName() {
        return this.conditionName;
    }

    @Override
    public int getIntensityScore() {
        return this.intensityScore;
    }

    @Override
    public abstract double getWeightedIntensityScore();
}
