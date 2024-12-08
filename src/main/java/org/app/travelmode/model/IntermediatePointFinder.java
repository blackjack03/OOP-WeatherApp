package org.app.travelmode.model;

import org.app.travelmode.directions.DirectionsLeg;
import org.app.travelmode.directions.SimpleDirectionsStep;

import java.util.List;

public interface IntermediatePointFinder {

    /**
     * Finds intermediate points for a given route leg.
     *
     * @param directionsLeg the {@link DirectionsLeg} to analyze
     * @param subStepGenerator the generator for creating sub-steps from a DirectionsStep
     * @return a list of intermediate points as {@link SimpleDirectionsStep} objects
     */
    List<SimpleDirectionsStep> findIntermediatePoints(DirectionsLeg directionsLeg, SubStepGenerator subStepGenerator);
}
