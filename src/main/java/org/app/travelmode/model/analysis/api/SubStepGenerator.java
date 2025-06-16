package org.app.travelmode.model.analysis;

import org.app.travelmode.model.google.dto.directions.DirectionsStep;
import org.app.travelmode.model.google.dto.directions.SimpleDirectionsStep;

import java.util.List;

/**
 * Defines a contract for breaking down a navigation step into smaller sub-steps
 * to provide more granular route information.
 *
 * <p>This generator is responsible for:
 * <ul>
 *     <li>Segmenting a route step into smaller units</li>
 *     <li>Calculating intermediate points</li>
 *     <li>Preserving navigation timing information</li>
 * </ul>
 */
public interface SubStepGenerator {

    /**
     * Generates sub-steps for a given {@link DirectionsStep}.
     *
     * @param step the {@link DirectionsStep} to be divided into sub-steps
     * @return a list of sub-steps represented as {@link SimpleDirectionsStep} objects
     */
    List<SimpleDirectionsStep> generateSubSteps(DirectionsStep step);

}
