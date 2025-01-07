package org.app.travelmode.model;

import org.app.travelmode.directions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class RouteAnalyzerImpl implements RouteAnalyzer {

    private static final BigDecimal DELTA = BigDecimal.valueOf(2000.0);
    private static final BigDecimal TARGET_DISTANCE = BigDecimal.valueOf(30000.0);
    private static final BigDecimal SUBSTEP_DISTANCE = BigDecimal.valueOf(1000.0);

    public RouteAnalyzerImpl() {

    }

    @Override
    public List<SimpleDirectionsStep> calculateIntermediatePoints(final DirectionsRoute directionsRoute) {
        final List<DirectionsLeg> legs = directionsRoute.getLegs();
        final List<SimpleDirectionsStep> intermediatePoints = new ArrayList<>();

        BigDecimal distanceCounter = BigDecimal.ZERO;
        BigDecimal durationCounter = BigDecimal.ZERO;
        LatLng startPosition = legs.get(0).getStart_location();

        for (final DirectionsLeg leg : legs) {
            for (final DirectionsStep step : leg.getSteps()) {
                BigDecimal stepDistance = BigDecimal.valueOf(step.getDistance().getValue());
                BigDecimal stepDuration = BigDecimal.valueOf(step.getDuration().getValue());

                distanceCounter = distanceCounter.add(stepDistance);
                durationCounter = durationCounter.add(stepDuration);

                if (isWithinTargetDistance(distanceCounter)) {
                    intermediatePoints.add(new SimpleDirectionsStep(durationCounter.doubleValue(), step.getEnd_location(), startPosition, distanceCounter.doubleValue()));
                    distanceCounter = BigDecimal.ZERO;
                    durationCounter = BigDecimal.ZERO;
                    startPosition = step.getEnd_location();
                } else if (distanceCounter.compareTo(TARGET_DISTANCE.add(DELTA)) >= 0) {
                    distanceCounter = distanceCounter.subtract(stepDistance);
                    durationCounter = durationCounter.subtract(stepDuration);
                    for (final SimpleDirectionsStep subStep : generateSubSteps(step)) {
                        BigDecimal subStepDistance = BigDecimal.valueOf(subStep.getDistance().getValue());
                        BigDecimal subStepDuration = BigDecimal.valueOf(subStep.getDuration().getValue());

                        distanceCounter = distanceCounter.add(subStepDistance);
                        durationCounter = durationCounter.add(subStepDuration);

                        if (isWithinTargetDistance(distanceCounter)) {
                            intermediatePoints.add(new SimpleDirectionsStep(durationCounter.doubleValue(), subStep.getEnd_location(), startPosition, distanceCounter.doubleValue()));
                            distanceCounter = BigDecimal.ZERO;
                            durationCounter = BigDecimal.ZERO;
                            startPosition = subStep.getEnd_location();
                        }
                    }
                }
                System.out.println(step);
            }
        }
        System.out.println(intermediatePoints);
        return intermediatePoints;
    }

    private boolean isWithinTargetDistance(final BigDecimal distance) {
        return distance.compareTo(TARGET_DISTANCE.subtract(DELTA)) > 0 && distance.compareTo(TARGET_DISTANCE.add(DELTA)) < 0;
    }

    private List<SimpleDirectionsStep> generateSubSteps(final DirectionsStep directionsStep) {
        final List<SimpleDirectionsStep> subSteps = new ArrayList<>();
        final List<LatLng> decodedPoints = PolylineDecoder.decode(directionsStep.getPolyline().getPoints());

        LatLng startPoint = decodedPoints.get(0);
        int i;
        for (i = 1; i < decodedPoints.size(); i++) {
            LatLng actualPoint = decodedPoints.get(i);
            BigDecimal segmentDistance = GeographicDistanceCalculator.computeDistance(startPoint, actualPoint);

            if (segmentDistance.compareTo(SUBSTEP_DISTANCE) >= 0) {
                subSteps.add(new SimpleDirectionsStep(calculateSubStepDuration(directionsStep, segmentDistance), actualPoint, startPoint, segmentDistance.doubleValue()));
                startPoint = actualPoint;
            }
        }

        final LatLng finalPoint = decodedPoints.get(decodedPoints.size() - 1);
        if (!startPoint.equals(finalPoint)) {
            BigDecimal finalSegmentDistance = GeographicDistanceCalculator.computeDistance(startPoint, finalPoint);
            subSteps.add(new SimpleDirectionsStep(calculateSubStepDuration(directionsStep, finalSegmentDistance), finalPoint, startPoint, finalSegmentDistance.doubleValue()));
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
