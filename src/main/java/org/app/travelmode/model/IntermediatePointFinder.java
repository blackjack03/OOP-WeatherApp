package org.app.travelmode.model;

import org.app.travelmode.directions.DirectionsLeg;
import org.app.travelmode.directions.SimpleDirectionsStep;

import java.util.List;

public interface IntermediatePointFinder {

    List<SimpleDirectionsStep> findIntermediatePoints(DirectionsLeg directionsLeg, SubStepGenerator subStepGenerator);
}
