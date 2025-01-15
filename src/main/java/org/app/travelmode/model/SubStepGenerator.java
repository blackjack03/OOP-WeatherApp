package org.app.travelmode.model;

import org.app.travelmode.directions.DirectionsStep;
import org.app.travelmode.directions.SimpleDirectionsStep;

import java.util.List;

public interface SubStepGenerator {

    /**
     * Generates sub-steps for a given {@link DirectionsStep}.
     *
     * @param step the step to process
     * @return a list of sub-steps represented as {@link SimpleDirectionsStep} objects
     */
    List<SimpleDirectionsStep> generateSubSteps(DirectionsStep step);

}
