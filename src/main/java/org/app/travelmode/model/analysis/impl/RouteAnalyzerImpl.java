package org.app.travelmode.model.analysis.impl;

import org.app.travelmode.model.analysis.api.IntermediatePointFinder;
import org.app.travelmode.model.analysis.api.RouteAnalyzer;
import org.app.travelmode.model.analysis.api.SubStepGenerator;
import org.app.travelmode.model.google.dto.directions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link RouteAnalyzer} interface that processes routes and generates detailed intermediate points.
 * Delegates specific tasks to {@link SubStepGenerator} and {@link IntermediatePointFinder} classes.
 *
 * <p>This analyzer:
 * <ul>
 *     <li>Processes complete routes into detailed step sequences</li>
 *     <li>Generates intermediate navigation points</li>
 *     <li>Handles multi-leg routes</li>
 *     <li>Implements single-use pattern for route analysis</li>
 * </ul>
 *
 * <p>The analyzer follows a single-use pattern where each instance can process
 * only one route. This ensures data consistency and prevents accidental reuse.
 */
public class RouteAnalyzerImpl implements RouteAnalyzer {

    private final IntermediatePointFinder intermediatePointFinder;
    private final SubStepGenerator subStepGenerator;
    private boolean consumed;

    /**
     * Constructs a new {@link RouteAnalyzerImpl} with specified {@link IntermediatePointFinder}
     * and {@link SubStepGenerator} implementations.
     *
     * @param pointFinder      the intermediate point finder implementation
     * @param subStepGenerator the sub-step generator implementation
     */
    public RouteAnalyzerImpl(final IntermediatePointFinder pointFinder, final SubStepGenerator subStepGenerator) {
        this.intermediatePointFinder = pointFinder;
        this.subStepGenerator = subStepGenerator;
        this.consumed = false;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation:
     * <ul>
     *     <li>Processes each leg of the route sequentially</li>
     *     <li>Generates detailed intermediate points</li>
     *     <li>Combines results from all legs into a single sequence</li>
     *     <li>Marks the analyzer as consumed after processing</li>
     * </ul>
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isConsumed() {
        return this.consumed;
    }
}
