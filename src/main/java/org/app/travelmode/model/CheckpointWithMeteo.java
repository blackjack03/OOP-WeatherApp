package org.app.travelmode.model;

public interface CheckpointWithMeteo extends Checkpoint {

    /**
     * @return a {@link WeatherReport} for this checkpoint
     */
    WeatherReport getWeatherReport();

    /**
     * Returns a score between 0 and 100 calculated by combining the impact of each weather condition.
     * A score of 100 represents ideal weather, while 0 represents the worst-case scenario.
     *
     * @return an integer between 0 and 100 representing this score.
     */
    int getWeatherScore();
}
