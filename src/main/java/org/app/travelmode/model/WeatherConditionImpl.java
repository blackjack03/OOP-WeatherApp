package org.app.travelmode.model;

public class WeatherConditionImpl implements WeatherCondition {

    private final WeatherType weatherType;
    private final Intensity intensity;

    public WeatherConditionImpl(final WeatherType weatherType, final Intensity intensity) {
        this.weatherType = weatherType;
        this.intensity = intensity;
    }

    @Override
    public double getImpact() {
        return weatherType.getBaseImpact() * intensity.getMultiplier();
    }

    @Override
    public WeatherType getWeatherType() {
        return this.weatherType;
    }

    @Override
    public Intensity getIntensity() {
        return this.intensity;
    }
}
