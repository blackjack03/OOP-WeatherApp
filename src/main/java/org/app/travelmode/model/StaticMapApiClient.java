package org.app.travelmode.model;

import javafx.scene.image.Image;

import java.util.List;

public interface MapImageGenerator {

    /**
     * Creates an image representing a map with a route.
     * The route is provided with the main weather conditions that could be encountered during the journey.
     * The route is specified by the polyline parameter.
     * Checkpoints are the points where the weather conditions have been checked.
     *
     * @param checkpoints A list of {@link CheckpointWithMeteo} representing the points along the route where weather conditions have been verified
     * @param polyline    An encoded polyline representing the path to be displayed
     * @return an image representing a map
     */
    Image generateMapImage(List<CheckpointWithMeteo> checkpoints, String polyline);
}
