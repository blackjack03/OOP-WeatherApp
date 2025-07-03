package org.app.travelmode.model.google.api;

import javafx.scene.image.Image;
import org.app.travelmode.model.checkpoint.api.CheckpointWithMeteo;
import org.app.travelmode.model.exception.MapGenerationException;

import java.util.List;

/**
 * Defines a contract for generating static map images with weather-enhanced route visualization.
 */
public interface StaticMapApiClient {

    /**
     * Creates an image representing a map with a route.
     *
     * <p>The generated map includes:
     * <ul>
     *     <li>A base map with dimensions of 650x488 pixels at 2x scale</li>
     *     <li>Color-coded markers for each checkpoint based on weather conditions:
     *         <ul>
     *             <li>Yellow: Good weather conditions</li>
     *             <li>Orange: Bad weather conditions</li>
     *             <li>Red: Terrible weather conditions</li>
     *         </ul>
     *     </li>
     *     <li>An encoded polyline showing the route between checkpoints</li>
     *     <li>Italian language labels and interface</li>
     * </ul>
     *
     * @param checkpoints A list of {@link CheckpointWithMeteo} representing the points along the route
     *                    where weather conditions have been verified
     * @param polyline    An encoded polyline representing the route path to be displayed
     * @return a JavaFX {@link Image} object containing the generated map.
     * @throws MapGenerationException if any of the following errors occur:
     *                                <ul>
     *                                    <li>Error in communication with Google Maps API</li>
     *                                    <li>Invalid HTTP response from server</li>
     *                                    <li>Error while reading image data</li>
     *                                    <li>Any unexpected error during map generation</li>
     *                                </ul>
     */
    Image generateMapImage(List<CheckpointWithMeteo> checkpoints, String polyline) throws MapGenerationException;
}
