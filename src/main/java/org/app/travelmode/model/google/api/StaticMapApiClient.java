package org.app.travelmode.model.google.api;

import javafx.scene.image.Image;
import org.app.travelmode.model.checkpoint.api.CheckpointWithMeteo;

import java.util.List;

/**
 * Defines a contract for generating static map images with weather-enhanced route visualization.
 */
public interface StaticMapApiClient {

    /**
     * Creates an image representing a map with a route.
     * The route is provided with the main weather conditions that could be encountered during the journey.
     * The route is specified by the polyline parameter.
     * Checkpoints are the points where the weather conditions have been checked.
     *
     * @param checkpoints A list of {@link CheckpointWithMeteo} representing the points along the route where weather conditions have been verified
     * @param polyline    An encoded polyline representing the path to be displayed
     * @return a JavaFX Image object containing the generated map visualization.
     */
    Image generateMapImage(List<CheckpointWithMeteo> checkpoints, String polyline);
}
