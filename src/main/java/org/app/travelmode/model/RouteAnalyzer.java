package org.app.travelmode.model;

import org.app.travelmode.directions.DirectionsRoute;
import org.app.travelmode.directions.SimpleDirectionsStep;

import java.util.List;

/**
 * Defines a contract for analyzing routes and calculating
 * intermediate points along a navigation path.
 *
 * <p>This analyzer supports:
 * <ul>
 *     <li>Route segmentation into intermediate points</li>
 *     <li>Single-use processing pattern</li>
 *     <li>Equidistant point distribution</li>
 *     <li>Complex route analysis</li>
 * </ul>
 */
public interface RouteAnalyzer {

    /**
     * Calculates the intermediate points along the provided route.
     * The distance between each intermediate point will be approximately equal.
     *
     * @param directionsRoute the route to analyze
     * @return a list of intermediate points along the route, represented as {@link SimpleDirectionsStep} objects
     */
    List<SimpleDirectionsStep> calculateIntermediatePoints(DirectionsRoute directionsRoute);

    /**
     * Checks if this analyzer instance has already processed a route.
     *
     * @return true if this {@link RouteAnalyzer} has already been consumed,
     * false if it is still available for route analysis.
     */
    boolean isConsumed();

}
