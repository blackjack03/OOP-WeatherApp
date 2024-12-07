package org.app.travelmode.model;

import org.app.travelmode.directions.DirectionsLeg;
import org.app.travelmode.directions.DirectionsStep;
import org.app.travelmode.directions.LatLng;
import org.app.travelmode.directions.SimpleDirectionsStep;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class IntermediatePointFinderImpl implements IntermediatePointFinder {

    private static final BigDecimal DELTA = BigDecimal.valueOf(2000.0);
    private static final BigDecimal TARGET_DISTANCE = BigDecimal.valueOf(30000.0);

    private BigDecimal distanceCounter;
    private BigDecimal durationCounter;
    private LatLng startPoint;

    public IntermediatePointFinderImpl() {
        this.distanceCounter = BigDecimal.ZERO;
        this.durationCounter = BigDecimal.ZERO;
    }

    @Override
    public List<SimpleDirectionsStep> findIntermediatePoints(final DirectionsLeg directionsLeg, final SubStepGenerator subStepGenerator) {
        final List<SimpleDirectionsStep> intermediatePoints = new ArrayList<>();

        this.startPoint = directionsLeg.getStart_location();

        for (final DirectionsStep step : directionsLeg.getSteps()) {
            BigDecimal stepDistance = BigDecimal.valueOf(step.getDistance().getValue());
            BigDecimal stepDuration = BigDecimal.valueOf(step.getDuration().getValue());

            distanceCounter = distanceCounter.add(stepDistance);
            durationCounter = durationCounter.add(stepDuration);

            if (isWithinTargetDistance(distanceCounter)) {
                intermediatePoints.add(new SimpleDirectionsStep(durationCounter.doubleValue(), step.getEnd_location(), startPoint, distanceCounter.doubleValue()));
                resetCounters(step.getEnd_location());
            } else if (distanceCounter.compareTo(TARGET_DISTANCE.add(DELTA)) >= 0) {
                distanceCounter = distanceCounter.subtract(stepDistance);
                durationCounter = durationCounter.subtract(stepDuration);
                analyzeSubSteps(subStepGenerator.generateSubSteps(step), intermediatePoints);
            }
        }
        intermediatePoints.add(new SimpleDirectionsStep(durationCounter.doubleValue(), directionsLeg.getEnd_location(), startPoint, distanceCounter.doubleValue()));

        return intermediatePoints;
    }

    private boolean isWithinTargetDistance(final BigDecimal distance) {
        return distance.compareTo(TARGET_DISTANCE.subtract(DELTA)) > 0 && distance.compareTo(TARGET_DISTANCE.add(DELTA)) < 0;
    }

    private void analyzeSubSteps(final List<SimpleDirectionsStep> subSteps, final List<SimpleDirectionsStep> intermediatePoints) {

        for (final SimpleDirectionsStep subStep : subSteps) {
            BigDecimal subStepDistance = BigDecimal.valueOf(subStep.getDistance().getValue());
            BigDecimal subStepDuration = BigDecimal.valueOf(subStep.getDuration().getValue());

            distanceCounter = distanceCounter.add(subStepDistance);
            durationCounter = durationCounter.add(subStepDuration);

            if (isWithinTargetDistance(distanceCounter)) {
                intermediatePoints.add(new SimpleDirectionsStep(durationCounter.doubleValue(), subStep.getEnd_location(), startPoint, distanceCounter.doubleValue()));
                resetCounters(subStep.getEnd_location());
            }
        }
    }

    private void resetCounters(final LatLng newStartPoint) {
        this.distanceCounter = BigDecimal.ZERO;
        this.durationCounter = BigDecimal.ZERO;
        this.startPoint = newStartPoint;
    }
}
