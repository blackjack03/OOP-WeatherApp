package org.app.travelmode.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link WeatherReport} interface that represents a report
 * containing a list of weather conditions and provides methods to calculate
 * an overall weather score based on their impact.
 */
public class WeatherReportImpl implements WeatherReport {

    private static final double MAX_IMPACT = 100.0;
    private static final int MIN_SCORE = 0;
    private static final int MAX_SCORE = 100;

    private final List<WeatherCondition> weatherConditions;
    private Optional<Integer> weatherScore;

    public WeatherReportImpl() {
        this.weatherConditions = new ArrayList<>();
        this.weatherScore = Optional.empty();
    }

    @Override
    public void addCondition(final WeatherCondition condition) {
        this.weatherConditions.add(condition);
    }

    @Override
    public void addConditions(final List<WeatherCondition> conditions) {
        this.weatherConditions.addAll(conditions);
    }

    @Override
    public List<WeatherCondition> getConditions() {
        return List.copyOf(this.weatherConditions);
    }

    @Override
    public int getWeatherScore() {
        return weatherScore.orElseGet(this::calculateWeatherScore);
    }

    @Override
    public int calculateWeatherScore() {
        if (this.weatherConditions.isEmpty()) {
            return MAX_SCORE;
        }

        double totalImpact = 0;
        double maxPossibleImpact = 0;

        for (final WeatherCondition condition : this.weatherConditions) {
            double impact = condition.getWeightedIntensityScore();
            totalImpact += impact;
            maxPossibleImpact += condition.getWorstWeightedIntensityScore();
        }

        double normalizedImpact = totalImpact / maxPossibleImpact;
        int score = (int) Math.round((1 - normalizedImpact) * 100);

        int finalScore = Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
        this.weatherScore = Optional.of(finalScore);
        return finalScore;
    }
}
