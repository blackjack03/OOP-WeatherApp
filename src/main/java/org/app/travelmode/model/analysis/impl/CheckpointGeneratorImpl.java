package org.app.travelmode.model.analysis.impl;

import org.app.travelmode.model.analysis.api.CheckpointGenerator;
import org.app.travelmode.model.google.dto.directions.LatLng;
import org.app.travelmode.model.google.dto.directions.SimpleDirectionsStep;
import org.app.travelmode.model.checkpoint.api.Checkpoint;
import org.app.travelmode.model.checkpoint.impl.CheckpointImpl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the {@link CheckpointGenerator} interface to create a sequence of checkpoints
 * based on navigation directions steps.
 *
 * <p>This generator creates checkpoints by:
 * <ul>
 *     <li>Converting route steps into geographical checkpoints</li>
 *     <li>Calculating arrival times based on step durations</li>
 *     <li>Maintaining the chronological sequence of the route</li>
 * </ul>
 *
 * <p>The generation process:
 * <ol>
 *     <li>Creates an initial checkpoint from the first step's starting location</li>
 *     <li>Iteratively creates subsequent checkpoints using each step's end location</li>
 *     <li>Calculates arrival times by adding step durations progressively</li>
 * </ol>
 */
public class CheckpointGeneratorImpl implements CheckpointGenerator {

    /**
     * Constructs a new checkpoint generator.
     */
    public CheckpointGeneratorImpl() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Checkpoint> generateCheckpoints(final List<SimpleDirectionsStep> steps, final ZonedDateTime departureDateTime) {
        final List<Checkpoint> checkpoints = new ArrayList<>();
        final SimpleDirectionsStep firstStep = steps.get(0);
        checkpoints.add(createCheckpoint(firstStep.getStartLocation(), departureDateTime));
        for (int i = 0; i < steps.size(); i++) {
            final SimpleDirectionsStep step = steps.get(i);
            final Checkpoint previousCheckpoint = checkpoints.get(i);
            checkpoints.add(createNextCheckpoint(previousCheckpoint, step));
        }
        return List.copyOf(checkpoints);
    }

    /**
     * Creates a new checkpoint from a geographical position and arrival time.
     *
     * @param position        the geographical coordinates of the checkpoint
     * @param arrivalDateTime the expected arrival time at this position
     * @return a new checkpoint instance
     */
    private Checkpoint createCheckpoint(final LatLng position, final ZonedDateTime arrivalDateTime) {
        return new CheckpointImpl(position.getLat(), position.getLng(), arrivalDateTime);
    }

    /**
     * Creates the next checkpoint in sequence based on the previous checkpoint
     * and the current navigation step.
     *
     * <p>The arrival time for the new checkpoint is calculated by adding
     * the step's duration to the previous checkpoint's arrival time.
     *
     * @param previousCheckpoint the last created checkpoint in the sequence
     * @param step               the current navigation step containing the next location and duration
     * @return a new checkpoint with calculated arrival time
     */
    private Checkpoint createNextCheckpoint(final Checkpoint previousCheckpoint, final SimpleDirectionsStep step) {
        return createCheckpoint(
                step.getEndLocation(),
                previousCheckpoint.getArrivalDateTime().plusSeconds((long) step.getDuration().getValue())
        );
    }
}
