package org.app.travelmode.model;

/**
 * Service interface for enriching a travel checkpoint with weather information.
 * Provides functionality to add weather data to a route checkpoint for travel planning
 * and route analysis.
 *
 * <p>This service is responsible for:
 * <ul>
 *     <li>Retrieving weather forecasts for specific locations and times</li>
 *     <li>Integrating weather data with a route checkpoint</li>
 *     <li>Creating enhanced checkpoint objects with weather information</li>
 * </ul>
 */
public interface WeatherInformationService {

    /**
     * Enriches the given checkpoint with weather data and returns a new checkpoint instance with a weather report.
     *
     * @param checkpoint the checkpoint to enrich
     * @return the enriched checkpoint with weather information
     */
    CheckpointWithMeteo enrichWithWeather(Checkpoint checkpoint);
}
