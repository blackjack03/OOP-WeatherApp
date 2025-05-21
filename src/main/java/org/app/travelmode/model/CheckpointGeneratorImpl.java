package org.app.travelmode.model;

import org.app.travelmode.directions.LatLng;
import org.app.travelmode.directions.SimpleDirectionsStep;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class CheckpointGeneratorImpl implements CheckpointGenerator {

    public CheckpointGeneratorImpl() {
    }

    @Override
    public List<Checkpoint> generateCheckpoints(final List<SimpleDirectionsStep> steps, final ZonedDateTime departureDateTime) {
        final List<Checkpoint> checkpoints = new ArrayList<>();
        final SimpleDirectionsStep firstStep = steps.get(0);
        checkpoints.add(createCheckpoint(firstStep.getStart_location(), departureDateTime));
        for (int i = 0; i < steps.size(); i++) {
            final SimpleDirectionsStep step = steps.get(i);
            final Checkpoint previousCheckpoint = checkpoints.get(i);
            checkpoints.add(createNextCheckpoint(previousCheckpoint, step));
        }
        return List.copyOf(checkpoints);
    }

    private Checkpoint createCheckpoint(final LatLng position, final ZonedDateTime arrivalDateTime) {
        return new CheckpointImpl(position.getLat(), position.getLng(), arrivalDateTime);
    }

    private Checkpoint createNextCheckpoint(final Checkpoint previousCheckpoint, final SimpleDirectionsStep step) {
        return createCheckpoint(
                step.getEnd_location(),
                previousCheckpoint.getArrivalDateTime().plusSeconds((long) step.getDuration().getValue())
        );
    }
}
