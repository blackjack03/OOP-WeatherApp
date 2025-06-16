package org.app.travelmode.model.analysis.api;

import org.app.travelmode.model.google.dto.directions.SimpleDirectionsStep;
import org.app.travelmode.model.checkpoint.api.Checkpoint;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Defines a generator for creating a sequence of geographic checkpoints from navigation directions.
 *
 * <p>This interface provides functionality to convert navigation route steps into a series
 * of checkpoints with calculated arrival times. Each checkpoint represents a significant
 * point along the route with its associated timing information.
 */
public interface CheckpointGenerator {

    /**
     * Generates a list of checkpoints based on the provided route steps and the departure time.
     *
     * <p>The method processes the provided route steps to create a chronological sequence of checkpoints.
     *
     * @param steps             the list of {@link SimpleDirectionsStep} to process.
     * @param departureDateTime the initial departure time with timezone information.
     * @return an ordered list of {@link Checkpoint} representing the route's progression.
     * The list maintains the same sequence as the input steps.
     */
    List<Checkpoint> generateCheckpoints(List<SimpleDirectionsStep> steps, ZonedDateTime departureDateTime);
}
