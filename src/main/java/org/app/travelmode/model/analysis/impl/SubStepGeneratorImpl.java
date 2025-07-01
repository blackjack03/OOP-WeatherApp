package org.app.travelmode.model.analysis.impl;

import org.app.travelmode.model.utility.GeographicDistanceCalculator;
import org.app.travelmode.model.utility.PolylineDecoder;
import org.app.travelmode.model.analysis.api.SubStepGenerator;
import org.app.travelmode.model.google.dto.directions.DirectionsStep;
import org.app.travelmode.model.google.dto.directions.LatLng;
import org.app.travelmode.model.google.dto.directions.SimpleDirectionsStep;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link SubStepGenerator} interface that breaks down {@link DirectionsStep}
 * into smaller, equally-sized segments.
 *
 * <p>This generator:
 * <ul>
 *     <li>Divides a route step into sub-steps of approximately 1000 meters each</li>
 *     <li>Calculates proportional duration for each sub-step</li>
 *     <li>Maintains precise geographic coordinates</li>
 * </ul>
 */
public class SubStepGeneratorImpl implements SubStepGenerator {

    private static final BigDecimal SUBSTEP_DISTANCE = BigDecimal.valueOf(1000.0);

    /**
     * Constructs a new {@link SubStepGeneratorImpl} with default settings.
     */
    public SubStepGeneratorImpl() {
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation:
     * <ul>
     *     <li>Decodes the step's polyline into a sequence of coordinates</li>
     *     <li>Creates sub-steps of approximately 1000 meters each</li>
     *     <li>Calculates proportional duration for each sub-step</li>
     *     <li>Ensures the final point is always included</li>
     * </ul>
     */
    @Override
    public List<SimpleDirectionsStep> generateSubSteps(final DirectionsStep step) {
        final List<SimpleDirectionsStep> subSteps = new ArrayList<>();
        final List<LatLng> decodedPoints = PolylineDecoder.decode(step.getPolyline().getPoints());

        LatLng startPoint = decodedPoints.get(0);
        for (int i = 1; i < decodedPoints.size(); i++) {
            final LatLng actualPoint = decodedPoints.get(i);
            final BigDecimal segmentDistance = GeographicDistanceCalculator.computeDistance(startPoint, actualPoint);

            if (segmentDistance.compareTo(SUBSTEP_DISTANCE) >= 0) {
                subSteps.add(new SimpleDirectionsStep(calculateSubStepDuration(step, segmentDistance), actualPoint, startPoint, segmentDistance.doubleValue()));
                startPoint = actualPoint;
            }
        }

        final LatLng finalPoint = decodedPoints.get(decodedPoints.size() - 1);
        if (!startPoint.equals(finalPoint)) {
            BigDecimal finalSegmentDistance = GeographicDistanceCalculator.computeDistance(startPoint, finalPoint);
            subSteps.add(new SimpleDirectionsStep(calculateSubStepDuration(step, finalSegmentDistance), finalPoint, startPoint, finalSegmentDistance.doubleValue()));
        }

        return subSteps;
    }

    /**
     * Calculates the duration of a sub-step based on its proportional distance
     * relative to the original step.
     *
     * <p>The calculation:
     * <ul>
     *     <li>Uses proportional scaling based on distance</li>
     *     <li>Maintains temporal consistency with the original step</li>
     *     <li>Rounds results to one decimal place</li>
     * </ul>
     *
     * @param directionsStep the original step containing duration information
     * @param subStepLength  the length of the sub-step in meters
     * @return the calculated duration of the sub-step in seconds
     */
    private double calculateSubStepDuration(final DirectionsStep directionsStep, final BigDecimal subStepLength) {
        final BigDecimal stepDuration = BigDecimal.valueOf(directionsStep.getDuration().getValue());
        final BigDecimal stepDistance = BigDecimal.valueOf(directionsStep.getDistance().getValue());
        final BigDecimal subStepDuration = subStepLength.multiply(stepDuration).divide(stepDistance, 1, RoundingMode.HALF_UP);

        return subStepDuration.doubleValue();
    }
}
