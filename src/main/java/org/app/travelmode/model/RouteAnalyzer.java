package org.app.travelmode.model;

import org.app.travelmode.directions.DirectionsRoute;
import org.app.travelmode.directions.SimpleDirectionsStep;

import java.util.List;

public interface RouteAnalyzer {

    /**
     * Calculates the intermediate points along the provided route. The distance between each intermediate point will be approximately equal.
     *
     * @param directionsRoute the route to analyze
     * @return A list of intermediate points represented as {@link SimpleDirectionsStep} objects
     */
    List<SimpleDirectionsStep> calculateIntermediatePoints(DirectionsRoute directionsRoute);

    /**
     * @return true if this {@link RouteAnalyzer} has already been consumed
     */
    boolean isConsumed();

}
