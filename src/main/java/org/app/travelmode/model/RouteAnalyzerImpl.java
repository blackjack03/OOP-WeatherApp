package org.app.travelmode.model;

import org.app.travelmode.directions.*;

import java.util.ArrayList;
import java.util.List;

public class RouteAnalyzerImpl implements RouteAnalyzer {

    private final IntermediatePointFinder intermediatePointFinder;
    private final SubStepGenerator subStepGenerator;

    public RouteAnalyzerImpl(final IntermediatePointFinder pointFinder, final SubStepGenerator subStepGenerator) {
        this.intermediatePointFinder = pointFinder;
        this.subStepGenerator = subStepGenerator;
    }

    @Override
    public List<SimpleDirectionsStep> calculateIntermediatePoints(final DirectionsRoute directionsRoute) {
        final List<SimpleDirectionsStep> intermediatePoints = new ArrayList<>();
        final List<DirectionsLeg> legs = directionsRoute.getLegs();

        for (final DirectionsLeg leg : legs) {
            intermediatePoints.addAll(intermediatePointFinder.findIntermediatePoints(leg, subStepGenerator));
        }

        return intermediatePoints;
    }

}
