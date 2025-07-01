package org.app.travelmode.model.analysis.impl;

import org.app.travelmode.model.analysis.api.IntermediatePointFinder;
import org.app.travelmode.model.analysis.api.SubStepGenerator;
import org.app.travelmode.model.google.dto.directions.DirectionsLeg;
import org.app.travelmode.model.google.dto.directions.DirectionsStep;
import org.app.travelmode.model.google.dto.directions.LatLng;
import org.app.travelmode.model.google.dto.directions.SimpleDirectionsStep;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IntermediatePointFinder} that calculates intermediate points
 * along a route based on target distances.
 *
 * <p>This implementation:
 * <ul>
 *     <li>Finds points approximately every 30km (±2km tolerance)</li>
 *     <li>Tracks cumulative distance and duration</li>
 *     <li>Handles route segmentation and sub-step analysis</li>
 *     <li>Maintains precise distance calculations using BigDecimal</li>
 * </ul>
 */
public class IntermediatePointFinderImpl implements IntermediatePointFinder {

    private static final BigDecimal DELTA = BigDecimal.valueOf(2000.0);
    private static final BigDecimal TARGET_DISTANCE = BigDecimal.valueOf(30000.0);

    private BigDecimal distanceCounter;
    private BigDecimal durationCounter;
    private LatLng startPoint;

    /**
     * Constructs a new intermediate point finder.
     */
    public IntermediatePointFinderImpl() {
        this.distanceCounter = BigDecimal.ZERO;
        this.durationCounter = BigDecimal.ZERO;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The method processes the route by:
     * <ul>
     *     <li>Analyzing each step in the route</li>
     *     <li>Accumulating distance and duration</li>
     *     <li>Creating intermediate points at target intervals</li>
     *     <li>Breaking down large steps into sub-steps when needed</li>
     * </ul>
     *
     * @param directionsLeg    the route leg to analyze
     * @param subStepGenerator generator for creating sub-steps when needed
     * @return an immutable list of intermediate points along the route
     */
    @Override
    @SuppressWarnings("checkstyle:MagicNumber")
    public List<SimpleDirectionsStep> findIntermediatePoints(final DirectionsLeg directionsLeg, final SubStepGenerator subStepGenerator) {
        final List<SimpleDirectionsStep> intermediatePoints = new ArrayList<>();

        this.startPoint = directionsLeg.getStart_location();

        for (final DirectionsStep step : directionsLeg.getSteps()) {
            final BigDecimal stepDistance = BigDecimal.valueOf(step.getDistance().getValue());
            final BigDecimal stepDuration = BigDecimal.valueOf(step.getDuration().getValue());

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

        final LatLng endPoint = directionsLeg.getEnd_location();
        if (!startPoint.equals(endPoint) || directionsLeg.getSteps().size() == 1) {
            intermediatePoints.add(new SimpleDirectionsStep(durationCounter.doubleValue(), endPoint, startPoint, distanceCounter.doubleValue()));
        }

        return List.copyOf(intermediatePoints);
    }

    /**
     * Checks whether the current distance is within the target range.
     *
     * @param distance the distance to check, in meters.
     * @return true if the distance is within the target range; false otherwise.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private boolean isWithinTargetDistance(final BigDecimal distance) {
        return distance.compareTo(TARGET_DISTANCE.subtract(DELTA)) > 0 && distance.compareTo(TARGET_DISTANCE.add(DELTA)) < 0;
    }

    /**
     * Processes a list of sub-steps to find intermediate points when a step
     * exceeds the target distance.
     *
     * <p>This method:
     * <ul>
     *     <li>Accumulates distance and duration for each sub-step</li>
     *     <li>Creates intermediate points at target intervals</li>
     *     <li>Updates tracking information when points are created</li>
     * </ul>
     *
     * @param subSteps           The list of sub-steps to analyze
     * @param intermediatePoints the list of intermediate points to update
     */
    private void analyzeSubSteps(final List<SimpleDirectionsStep> subSteps, final List<SimpleDirectionsStep> intermediatePoints) {

        for (final SimpleDirectionsStep subStep : subSteps) {
            final BigDecimal subStepDistance = BigDecimal.valueOf(subStep.getDistance().getValue());
            final BigDecimal subStepDuration = BigDecimal.valueOf(subStep.getDuration().getValue());

            distanceCounter = distanceCounter.add(subStepDistance);
            durationCounter = durationCounter.add(subStepDuration);

            if (isWithinTargetDistance(distanceCounter)) {
                intermediatePoints.add(new SimpleDirectionsStep(durationCounter.doubleValue(), subStep.getEnd_location(), startPoint, distanceCounter.doubleValue()));
                resetCounters(subStep.getEnd_location());
            }
        }
    }

    /**
     * Resets the internal counters and updates the starting position for the
     * next distance calculation segment.
     *
     * @param newStartPoint the new starting position for the next segment.
     */
    private void resetCounters(final LatLng newStartPoint) {
        this.distanceCounter = BigDecimal.ZERO;
        this.durationCounter = BigDecimal.ZERO;
        this.startPoint = newStartPoint;
    }
}
