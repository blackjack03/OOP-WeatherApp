package org.app.travelmode.model;

import java.util.List;

public interface WeatherReport {

    /**
     * Adds a new weather condition to the report.
     *
     * @param condition the {@link WeatherCondition} to be added.
     */
    void addCondition(WeatherCondition condition);

    /**
     * Adds a {@link List} of conditions to the report
     *
     * @param conditions the list of {@link WeatherCondition} to be added.
     */
    void addConditions(List<WeatherCondition> conditions);

    /**
     * Returns an immutable list of the weather conditions in the report.
     *
     * @return an immutable {@link List} of {@link WeatherCondition}.
     */
    List<WeatherCondition> getConditions();

    /**
     * Return a score from 0 to 100 based on the combined impact of weather conditions.
     * A score of 100 represents ideal weather, while 0 represents the worst-case scenario.
     *
     * @return an integer score between 0 and 100 representing this score
     */
    int getWeatherScore();

    /**
     * Calculates a score from 0 to 100 based on the combined impact of weather conditions.
     * A score of 100 represents ideal weather, while 0 represents the worst-case scenario.
     *
     * @return an integer score between 0 and 100 representing this score
     */
    int calculateWeatherScore();
}
