package org.app.travelmode.model.weather.impl;

import org.app.travelmode.model.weather.api.WeatherCondition;
import org.app.travelmode.model.weather.api.WeatherReport;

import java.util.List;

/**
 * Immutable implementation of the {@link WeatherReport} interface that represents a report
 * containing a list of weather conditions and provides methods to calculate
 * an overall weather score based on their impact.
 *
 * <p>Once created, the list of {@link WeatherCondition}s and the derived score cannot be altered.</p>
 */
public final class WeatherReportImpl implements WeatherReport {

    private static final int MIN_SCORE = 0;
    private static final int MAX_SCORE = 100;
    private static final int MULTIPLIER = 50;
    private static final double VERTICAL_SHIFT = 227.12;
    private static final double HORIZONTAL_SHIFT = 93.91;

    private final List<WeatherCondition> weatherConditions;
    private final int weatherScore;

    /**
     * Builds an empty immutable report whose score is the maximum (perfect weather).
     */
    public WeatherReportImpl() {
        this.weatherConditions = List.of();
        this.weatherScore = MAX_SCORE;
    }

    /**
     * Builds a report from a list of conditions. A defensive, unmodifiable copy
     * of the list is stored internally.
     *
     * @param conditions list of weather conditions
     */
    public WeatherReportImpl(final List<WeatherCondition> conditions) {
        this.weatherConditions = List.copyOf(conditions);
        this.weatherScore = this.calculateWeatherScore();
    }

    /**
     * Builds a copy of the {@link WeatherReport} provided as input.
     *
     * @param source the {@link WeatherReport} to copy.
     */
    public WeatherReportImpl(final WeatherReport source) {
        this(source.getConditions());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WeatherCondition> getConditions() {
        return this.weatherConditions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWeatherScore() {
        return this.weatherScore;
    }

    /**
     * Calculates a score from 0 to 100 based on the combined impact of weather conditions.
     * A score of 100 represents ideal weather, while 0 represents the worst-case scenario.
     *
     * @return an integer score between 0 and 100 representing this score
     */
    private int calculateWeatherScore() {
        if (this.weatherConditions.isEmpty()) {
            return MAX_SCORE;
        }

        final double totalImpact = this.weatherConditions.stream()
                .mapToDouble(WeatherCondition::getWeightedIntensityScore)
                .sum();
        final long normalizedImpact = Math.round(MULTIPLIER * Math.log(totalImpact + HORIZONTAL_SHIFT) - VERTICAL_SHIFT);

        return (int) Math.min(MAX_SCORE, Math.max(MIN_SCORE, MAX_SCORE - normalizedImpact));
    }
}
