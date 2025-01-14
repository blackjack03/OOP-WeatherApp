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

    private final List<WeatherCondition> weatherConditions;

    public WeatherReportImpl() {
        this.weatherConditions = new ArrayList<>();
    }

    @Override
    public void addCondition(final WeatherCondition condition) {
        this.weatherConditions.add(condition);
    }

    @Override
    public List<WeatherCondition> getConditions() {
        return List.copyOf(this.weatherConditions);
    }

    @Override
    public int calculateWeatherScore() {
        if (this.weatherConditions.isEmpty()) {
            return MAX_SCORE;
        }

        double totalImpact = 0;
        double maxPossibleImpact = 0;

        for (final WeatherCondition condition : this.weatherConditions) {
            double impact = condition.getImpact();
            totalImpact += impact;
            maxPossibleImpact += MAX_IMPACT;
        }

        double normalizedImpact = totalImpact / maxPossibleImpact;
        int score = (int) Math.round((1 - normalizedImpact) * 100);

        return Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
    }
}
