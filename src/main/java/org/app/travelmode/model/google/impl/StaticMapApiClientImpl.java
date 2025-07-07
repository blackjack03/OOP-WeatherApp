package org.app.travelmode.model.google.impl;

import javafx.scene.image.Image;
import org.app.travelmode.model.checkpoint.api.CheckpointWithMeteo;
import org.app.travelmode.model.exception.MapGenerationException;
import org.app.travelmode.model.google.api.GoogleApiRequestBuilder;
import org.app.travelmode.model.google.api.StaticMapApiClient;
import org.app.travelmode.model.weather.impl.conditions.WeatherScoreCategory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.util.List;

/**
 * Implementation of {@link StaticMapApiClient} that generates static maps using Google Maps Static API.
 * This class handles the creation of map images with custom markers and route polylines.
 */
public class StaticMapApiClientImpl extends AbstractGoogleApiClient implements StaticMapApiClient {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/staticmap?";
    private static final String DEFAULT_SIZE = "650x488";
    private static final String DEFAULT_SCALE = "2";
    private static final String DEFAULT_LANGUAGE = "it";

    private final GoogleApiRequestBuilder requestBuilder;

    /**
     * Constructs a new StaticMapApiClientImpl with the specified API key.
     *
     * @param apiKey the Google Maps API key to use for authentication
     */
    public StaticMapApiClientImpl(final String apiKey) {
        super(BASE_URL, apiKey);
        this.requestBuilder = new GoogleApiRequestBuilderImpl(BASE_URL, apiKey);
    }

    /**
     * {@inheritDoc}
     *
     * @throws MapGenerationException if any of the following errors occur:
     *                                <ul>
     *                                    <li>Error in communication with Google Maps API</li>
     *                                    <li>Invalid HTTP response from server</li>
     *                                    <li>Error while reading image data</li>
     *                                    <li>Any unexpected error during map generation</li>
     *                                </ul>
     */
    @Override
    public Image generateMapImage(final List<CheckpointWithMeteo> checkpoints,
                                  final String polyline) throws MapGenerationException {
        try {
            final String url = buildMapUrl(checkpoints, polyline);
            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new MapGenerationException("Errore durante la richiesta della mappa: "
                        + connection.getResponseMessage());
            }

            final InputStream inputStream = connection.getInputStream();
            return new Image(inputStream);
        } catch (final IOException e) {
            throw new MapGenerationException("Errore I/O durante la generazione della mappa.", e);
        }
    }

    /**
     * Builds the complete URL for the static map request.
     *
     * @param checkpoints list of checkpoints to mark on the map
     * @param polyline    encoded polyline string for the route
     * @return the complete URL string for the static map request
     */
    private String buildMapUrl(final List<CheckpointWithMeteo> checkpoints, final String polyline) {
        this.requestBuilder.addParameter("size", DEFAULT_SIZE)
                .addParameter("scale", DEFAULT_SCALE)
                .addParameter("language", DEFAULT_LANGUAGE)
                .addParameter("path", "enc:" + polyline);
        checkpoints.forEach(this::addCheckpointMarker);
        return this.requestBuilder.build();
    }

    /**
     * Adds a marker for a checkpoint to the map request.
     * The marker color is determined by the weather score at the checkpoint.
     *
     * @param checkpoint the checkpoint with weather information to add to the map
     */
    private void addCheckpointMarker(final CheckpointWithMeteo checkpoint) {
        final String color = selectMarkerColor(checkpoint.getWeatherScore());
        if (color != null) {
            this.requestBuilder.addParameter("markers", String.format("color:%s|%f,%f",
                    color,
                    checkpoint.getLatitude(),
                    checkpoint.getLongitude())
            );
        }
    }

    /**
     * Selects the appropriate marker color based on the weather score.
     *
     * @param weatherScore the weather score to evaluate
     * @return the color string to use for the marker, or null if no color is applicable
     */
    private String selectMarkerColor(final int weatherScore) {
        return switch (WeatherScoreCategory.fromScore(weatherScore)) {
            case GOOD -> "yellow";
            case BAD -> "orange";
            case TERRIBLE -> "red";
            default -> null;
        };
    }

}

