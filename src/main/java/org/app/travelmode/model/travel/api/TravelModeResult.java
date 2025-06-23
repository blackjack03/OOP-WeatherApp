package org.app.travelmode.model.travel.api;

import javafx.scene.image.Image;
import org.app.travelmode.model.checkpoint.api.CheckpointWithMeteo;
import org.app.travelmode.model.exception.MapGenerationException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the result of the travel mode analysis, containing route information,
 * weather conditions, timing details, and visualization data.
 */
public interface TravelModeResult {

    /**
     * Returns an immutable list of the route's checkpoints.
     *
     * @return an immutable list of {@link CheckpointWithMeteo}.
     */
    List<CheckpointWithMeteo> getCheckpoints();

    /**
     * Generates and returns the map image representing the trip's route.
     * The image is cached after the first generation.
     *
     * @return an {@link Image} object representing the trip's map.
     * @throws MapGenerationException if an error occurs while generating the map
     */
    Image getMapImage() throws MapGenerationException;

    /**
     * Calculates and returns the average weather score based on the checkpoints.
     *
     * @return an integer from 0 to 100 representing the average weather score.
     * @throws IllegalArgumentException if the list of checkpoints is empty.
     */
    int getMeteoScore();

    /**
     * Returns the trip's summary.
     *
     * @return a string containing the trip's summary.
     */
    String getSummary();

    /**
     * Returns the duration of the trip.
     *
     * @return a {@link Duration} object representing the trip's duration.
     */
    Duration getDuration();

    /**
     * Returns the estimated arrival time for the trip.
     *
     * @return a {@link LocalDateTime} object representing the estimated arrival time.
     */
    LocalDateTime getArrivalTime();

    /**
     * Return a string like "hh ore mm minuti":
     * hh represents the number of hours, and mm represents the number of minutes.
     *
     * @return a {@link String} representing the duration of the trip in the format "hh ore mm minuti".
     */
    String getDurationString();
}
