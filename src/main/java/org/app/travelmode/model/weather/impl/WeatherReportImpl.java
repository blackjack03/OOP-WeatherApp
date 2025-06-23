package org.app.travelmode.model.weather.impl;

import org.app.travelmode.model.weather.api.WeatherCondition;
import org.app.travelmode.model.weather.api.WeatherReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link WeatherReport} interface that represents a report
 * containing a list of weather conditions and provides methods to calculate
 * an overall weather score based on their impact.
 */
public class WeatherReportImpl implements WeatherReport {

    private static final int MIN_SCORE = 0;
    private static final int MAX_SCORE = 100;
    private static final int MULTIPLIER = 50;
    private static final double VERTICAL_SHIFT = 227.12;
    private static final double HORIZONTAL_SHIFT = 93.91;

    private final List<WeatherCondition> weatherConditions;
    private Optional<Integer> weatherScore;

    /**
     * Constructs a new {@link WeatherReportImpl}
     */
    public WeatherReportImpl() {
        this.weatherConditions = new ArrayList<>();
        this.weatherScore = Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCondition(final WeatherCondition condition) {
        this.weatherConditions.add(condition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addConditions(final List<WeatherCondition> conditions) {
        this.weatherConditions.addAll(conditions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<WeatherCondition> getConditions() {
        return List.copyOf(this.weatherConditions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWeatherScore() {
        if (this.weatherScore.isEmpty()) {
            this.weatherScore = Optional.of(calculateWeatherScore());
        }
        return this.weatherScore.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int calculateWeatherScore() {
        if (this.weatherConditions.isEmpty()) {
            return MAX_SCORE;
        }

        if (this.weatherScore.isEmpty()) {
            double totalImpact = this.weatherConditions.stream()
                    .mapToDouble(WeatherCondition::getWeightedIntensityScore)
                    .sum();
            long normalizedImpact = Math.round(MULTIPLIER * Math.log(totalImpact + HORIZONTAL_SHIFT) - VERTICAL_SHIFT); //TODO: valutare il moltiplicatore
            int score = (int) Math.min(MAX_SCORE, Math.max(MIN_SCORE, MAX_SCORE - normalizedImpact));
            this.weatherScore = Optional.of(score);
        }

        return this.weatherScore.get();
    }
}
