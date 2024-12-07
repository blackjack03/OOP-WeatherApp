package org.app.travelmode.model;

import org.app.travelmode.directions.DirectionsStep;
import org.app.travelmode.directions.SimpleDirectionsStep;

import java.util.List;

public interface SubStepGenerator {

    List<SimpleDirectionsStep> generateSubSteps(DirectionsStep step);
}
