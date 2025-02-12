package org.app.travelmode.model;

public class WeatherConditionFactoryImpl implements WeatherConditionFactory {

    @Override
    public FreezingRisk createFreezingRisk(double freezingHeightMetres) {
        return new FreezingRisk(freezingHeightMetres);
    }

    @Override
    public Precipitation createPrecipitation(double precipitationMm) {
        return new Precipitation(precipitationMm);
    }

    @Override
    public Snowfall createSnowfall(double snowfallCm) {
        return new Snowfall(snowfallCm);
    }

    @Override
    public Visibility createVisibility(double visibilityMeter) {
        return new Visibility(visibilityMeter);
    }

    @Override
    public WindGust createWindGust(double windGustSpeed) {
        return new WindGust(windGustSpeed);
    }
}
