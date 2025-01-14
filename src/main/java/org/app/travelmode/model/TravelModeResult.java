package org.app.travelmode.model;

import javafx.scene.image.Image;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface TravelModeResult {

    /**
     * Returns an immutable list of the route's checkpoints.
     *
     * @return an immutable list of {@link CheckpointWithMeteo}.
     */
    List<CheckpointWithMeteo> getCheckpoints();

    /**
     * Generates and returns the map image representing the trip's route.
     *
     * @return an {@link Image} object representing the trip's map.
     */
    Image getMapImage();

    /**
     * Calculates and returns the average weather score based on the checkpoints.
     *
     * @return an integer representing the average weather score.
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
}
