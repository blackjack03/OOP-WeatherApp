package org.app.travelmode.model.checkpoint.api;

import org.app.travelmode.model.weather.api.WeatherReport;

/**
 * Extends the {@link Checkpoint} interface to include weather information and analysis
 * capabilities for a geographical location at a specific time.
 *
 * <p>This interface adds meteorological functionality to basic checkpoints by providing:
 * <ul>
 *     <li>Access to detailed weather condition reports</li>
 *     <li>Numerical scoring of weather conditions</li>
 * </ul>
 *
 * <p>The weather information is specific to:
 * <ul>
 *     <li>The checkpoint's geographical location (latitude/longitude)</li>
 *     <li>The expected arrival time at the checkpoint</li>
 * </ul>
 */
public interface CheckpointWithMeteo extends Checkpoint {

    /**
     * Retrieves the weather report associated with this checkpoint.
     *
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
