package org.app.travelmode.model;

import org.app.travelmode.directions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link RouteAnalyzer} interface that analyzes routes and calculates intermediate points.
 * Delegates specific tasks to {@link SubStepGenerator} and {@link IntermediatePointFinder} classes.
 */
public class RouteAnalyzerImpl implements RouteAnalyzer {

    private final IntermediatePointFinder intermediatePointFinder;
    private final SubStepGenerator subStepGenerator;
    private boolean consumed;

    public RouteAnalyzerImpl(final IntermediatePointFinder pointFinder, final SubStepGenerator subStepGenerator) {
        this.intermediatePointFinder = pointFinder;
        this.subStepGenerator = subStepGenerator;
        this.consumed = false;
    }

    @Override
    public List<SimpleDirectionsStep> calculateIntermediatePoints(final DirectionsRoute directionsRoute) {
        if (consumed) {
            throw new IllegalStateException("Questo RouteAnalyzer è già stato consumato");
        }

        final List<SimpleDirectionsStep> intermediatePoints = new ArrayList<>();
        final List<DirectionsLeg> legs = directionsRoute.getLegs();

        for (final DirectionsLeg leg : legs) {
            intermediatePoints.addAll(intermediatePointFinder.findIntermediatePoints(leg, subStepGenerator));
        }

        this.consumed = true;
        return intermediatePoints;
    }

    @Override
    public boolean isConsumed() {
        return this.consumed;
    }
}
