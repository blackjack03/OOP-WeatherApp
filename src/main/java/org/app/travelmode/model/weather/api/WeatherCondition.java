package org.app.travelmode.model.weather.api;

/**
 * Represents a weather condition with its associated characteristics and scoring system.
 * This interface defines methods to retrieve information about a specific weather condition,
 * including its name, intensity, and weighted scores for risk assessment.
 */
public interface WeatherCondition {

    /**
     * Gets the name of the weather condition.
     *
     * @return the name of the weather condition as a {@link String}
     */
    String getConditionName();

    /**
     * Gets the raw intensity score of the weather condition.
     * The score typically ranges from 0 to 100, where:
     * 0 represents the most favorable conditions,
     * 100 represents the most severe conditions.
     *
     * @return the intensity score as an integer between 0 and 100
     */
    int getIntensityScore();

    /**
     * Gets the maximum possible weighted intensity score for this weather condition.
     * This represents the worst-case scenario for this particular condition,
     * taking into account its specific weight factor.
     *
     * @return the maximum possible weighted intensity score as a double
     */
    double getWorstWeightedIntensityScore();

    /**
     * Gets the actual weighted intensity score for the current weather condition.
     * This score is calculated by multiplying the raw intensity score by
     * a weight factor specific to this type of weather condition.
     *
     * @return the weighted intensity score as a double
     */
    double getWeightedIntensityScore();
}
