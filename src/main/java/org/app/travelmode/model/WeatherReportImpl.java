package org.app.travelmode.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link WeatherReport} interface that represents a report
 * containing a list of weather conditions and provides methods to calculate
 * an overall weather score based on their impact.
 */
public class WeatherReportImpl implements WeatherReport {

    private static final double MAX_IMPACT = 100.0;
    private static final int MIN_SCORE = 0;
    private static final int MAX_SCORE = 100;

    private final List<WeatherCondition> meteoConditions;

    public WeatherReportImpl() {
        this.meteoConditions = new ArrayList<>();
    }

    @Override
    public void addCondition(final WeatherCondition condition) {
        this.meteoConditions.add(condition);
    }

    @Override
    public void addConditions(final List<WeatherCondition> conditions) {
        this.meteoConditions.addAll(conditions);
    }

    @Override
    public List<WeatherCondition> getConditions() {
        return List.copyOf(this.meteoConditions);
    }

    @Override
    public int calculateWeatherScore() {
        if (this.meteoConditions.isEmpty()) {
            return MAX_SCORE;
        }

        double totalImpact = 0;
        double maxPossibleImpact = 0;

        for (final WeatherCondition condition : this.meteoConditions) {
            double impact = condition.getWeightedIntensityScore();
            totalImpact += impact;
            maxPossibleImpact += MAX_IMPACT;
        }

        double normalizedImpact = totalImpact / maxPossibleImpact;
        int score = (int) Math.round((1 - normalizedImpact) * 100);

        return Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
    }
}
