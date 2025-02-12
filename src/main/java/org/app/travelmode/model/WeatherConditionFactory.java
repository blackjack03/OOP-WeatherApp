package org.app.travelmode.model;

public interface WeatherConditionFactory {

    FreezingRisk createFreezingRisk(double freezingHeightMetres);

    Precipitation createPrecipitation(double precipitationMm);

    Snowfall createSnowfall(double snowfallCm);

    Visibility createVisibility(double visibilityMeter);

    WindGust createWindGust(double windGustSpeed);

}
