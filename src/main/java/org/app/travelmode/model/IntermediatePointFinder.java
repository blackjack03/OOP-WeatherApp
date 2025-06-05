package org.app.travelmode.model;

import org.app.travelmode.directions.DirectionsLeg;
import org.app.travelmode.directions.SimpleDirectionsStep;
import org.app.travelmode.directions.DirectionsStep;

import java.util.List;

/**
 * Defines a contract for finding intermediate points along a route segment.
 *
 * <p>The primary purpose of this interface is to:
 * <ul>
 *     <li>Break down long routes into manageable segments</li>
 *     <li>Identify strategic checkpoint locations</li>
 *     <li>Calculate cumulative distances and durations</li>
 *     <li>Support route analysis and optimization</li>
 * </ul>
 */
public interface IntermediatePointFinder {

    /**
     * Finds intermediate points for a given route leg.
     *
     * @param directionsLeg    the {@link DirectionsLeg} to analyze, containing detailed path information
     * @param subStepGenerator utility for breaking down complex {@link DirectionsStep} into smaller segments
     * @return an immutable list of intermediate points as {@link SimpleDirectionsStep} objects
     */
    List<SimpleDirectionsStep> findIntermediatePoints(DirectionsLeg directionsLeg, SubStepGenerator subStepGenerator);
}
