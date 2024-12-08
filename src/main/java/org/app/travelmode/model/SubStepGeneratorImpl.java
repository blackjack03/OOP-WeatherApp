package org.app.travelmode.model;

import org.app.travelmode.directions.DirectionsStep;
import org.app.travelmode.directions.LatLng;
import org.app.travelmode.directions.SimpleDirectionsStep;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class SubStepGeneratorImpl implements SubStepGenerator {

    private static final BigDecimal SUBSTEP_DISTANCE = BigDecimal.valueOf(1000.0);

    @Override
    public List<SimpleDirectionsStep> generateSubSteps(final DirectionsStep step) {
        final List<SimpleDirectionsStep> subSteps = new ArrayList<>();
        final List<LatLng> decodedPoints = PolylineDecoder.decode(step.getPolyline().getPoints());

        LatLng startPoint = decodedPoints.get(0);
        int i;
        for (i = 1; i < decodedPoints.size(); i++) {
            LatLng actualPoint = decodedPoints.get(i);
            BigDecimal segmentDistance = GeographicDistanceCalculator.computeDistance(startPoint, actualPoint);

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

    private double calculateSubStepDuration(final DirectionsStep directionsStep, BigDecimal subStepLength) {
        BigDecimal stepDuration = BigDecimal.valueOf(directionsStep.getDuration().getValue());
        BigDecimal stepDistance = BigDecimal.valueOf(directionsStep.getDistance().getValue());
        final BigDecimal subStepDuration = subStepLength.multiply(stepDuration).divide(stepDistance, 1, RoundingMode.HALF_UP);

        return subStepDuration.doubleValue();
    }
}
