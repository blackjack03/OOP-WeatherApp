package org.app.travelmode.model;

import org.app.travelmode.directions.SimpleDirectionsStep;

import java.time.ZonedDateTime;
import java.util.List;

public interface CheckpointGenerator {

    /**
     * Generates a list of checkpoints based on the provided route steps and the departure time.
     *
     * @param steps             the list of {@link SimpleDirectionsStep} to process.
     * @param departureDateTime the departure time.
     * @return a list of {@link Checkpoint} objects.
     */
    List<Checkpoint> generateCheckpoints(List<SimpleDirectionsStep> steps, ZonedDateTime departureDateTime);
}
