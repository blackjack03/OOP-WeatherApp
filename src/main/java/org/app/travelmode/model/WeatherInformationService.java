package org.app.travelmode.model;

public interface WeatherInformationService {

    /**
     * Enriches the given checkpoint with weather data and returns a new checkpoint instance with weather report.
     *
     * @param checkpoint the checkpoint to enrich
     * @return the enriched checkpoint with weather information
     */
    CheckpointWithMeteo enrichWithWeather(Checkpoint checkpoint);
}
