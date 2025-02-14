package org.app.travelmode.model;

public interface WeatherCondition {

    String getConditionName();

    int getIntensityScore();

    double getWorstWeightedIntensityScore();

    double getWeightedIntensityScore();
}
