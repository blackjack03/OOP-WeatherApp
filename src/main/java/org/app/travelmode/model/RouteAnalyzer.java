package org.app.travelmode.model;

import org.app.travelmode.directions.DirectionsRoute;
import org.app.travelmode.directions.SimpleDirectionsStep;

import java.util.List;

public interface RouteAnalyzer {

    /**
     * Group all the steps of an entire {@link DirectionsRoute} into {@link SimpleDirectionsStep}. Each of these will be approximately equal in length.
     *
     * @param directionsRoute represents the path
     * @return A list of SimpleDirectionsStep each of which will be approximately equal in length.
     */
    List<SimpleDirectionsStep> calculateIntermediatePoints(DirectionsRoute directionsRoute);

}
