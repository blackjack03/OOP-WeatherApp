package org.app.travelmode.model.weather.api;

import java.util.List;

/**
 * Represents a comprehensive weather report that aggregates multiple weather conditions
 * and provides analysis capabilities. This interface manages weather condition collections
 * and calculates weather quality scores.
 *
 * <p>The report provides functionality for:
 * <ul>
 *     <li>Managing multiple weather conditions</li>
 *     <li>Calculating overall weather quality scores</li>
 *     <li>Maintaining immutable access to conditions</li>
 * </ul>
 *
 * <p>Weather scoring is based on a 0-100 scale where:
 * <ul>
 *     <li>76-100: Excellent weather conditions</li>
 *     <li>51-75: Good weather conditions</li>
 *     <li>26-50: Bad weather conditions</li>
 *     <li>0-25: Terrible weather conditions</li>
 * </ul>
 */
public interface WeatherReport {

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
}
