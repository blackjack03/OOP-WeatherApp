package org.app.travelmode.model.travel.impl;

import javafx.scene.image.Image;
import org.app.travelmode.model.exception.MapGenerationException;
import org.app.travelmode.model.google.api.GoogleApiClientFactory;
import org.app.travelmode.model.google.impl.GoogleApiClientFactoryImpl;
import org.app.travelmode.model.checkpoint.api.CheckpointWithMeteo;
import org.app.travelmode.model.google.api.StaticMapApiClient;
import org.app.travelmode.model.travel.api.TravelModeResult;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link TravelModeResult} that provides comprehensive trip analysis results
 * including weather conditions, route visualization, and timing information.
 *
 * <p>This class manages:
 * <ul>
 *     <li>Route checkpoints with weather data</li>
 *     <li>Trip duration and arrival time</li>
 *     <li>Route visualization with static maps</li>
 *     <li>Weather score calculations</li>
 *     <li>Route summary information</li>
 * </ul>
 */
public class TravelModeResultImpl implements TravelModeResult {

    private static final int MINUTE_IN_HOURS = 60;

    private final List<CheckpointWithMeteo> checkpoints;
    private final String summary;
    private final Duration duration;
    private final LocalDateTime arrivalTime;
    private final String polyline;
    private final StaticMapApiClient mapImageGenerator;
    private Optional<Image> mapImage;
    private Optional<String> durationString;
    private Optional<Integer> meteoScore;

    /**
     * Constructs a new TravelModeResultImpl with the specified parameters.
     *
     * @param checkpoints list of checkpoints with weather information
     * @param summary     textual description of the route
     * @param polyline    encoded polyline string representing the route
     * @param duration    total duration of the trip
     */
    public TravelModeResultImpl(final List<CheckpointWithMeteo> checkpoints, final String summary,
                                final String polyline, final Duration duration) {
        this.checkpoints = checkpoints;
        this.arrivalTime = checkpoints.get(checkpoints.size() - 1).getArrivalDateTime().toLocalDateTime();
        this.summary = summary;
        this.duration = duration;
        this.polyline = polyline;
        this.mapImage = Optional.empty();
        this.durationString = Optional.empty();
        this.meteoScore = Optional.empty();
        final GoogleApiClientFactory googleApiClientFactory = new GoogleApiClientFactoryImpl();
        this.mapImageGenerator = googleApiClientFactory.createStaticMapApiClient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CheckpointWithMeteo> getCheckpoints() {
        return List.copyOf(checkpoints);
    }

    /**
     * {@inheritDoc}
     *
     * @throws MapGenerationException if an error occurs while generating the map
     */
    @Override
    public Image getMapImage() throws MapGenerationException {
        if (this.mapImage.isEmpty()) {
            this.mapImage = Optional.of(mapImageGenerator.generateMapImage(checkpoints, polyline));
        }
        return mapImage.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMeteoScore() {
        if (this.checkpoints.isEmpty()) {
            throw new IllegalStateException("La lista di checkpoint non può essere vuota.");
        }
        if (this.meteoScore.isEmpty()) {
            this.meteoScore = Optional.of(calculateMeteoScore(this.checkpoints));
        }
        return meteoScore.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSummary() {
        return this.summary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration getDuration() {
        return this.duration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getArrivalTime() {
        return this.arrivalTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDurationString() {
        if (this.durationString.isEmpty()) {
            this.durationString = Optional.of(formatDuration(duration));
        }
        return this.durationString.get();
    }

    /**
     * Create a string like "hh ore mm minuti" starting from a {@link Duration} object,
     * hh represents the number of hours and mm represents the number of minutes.
     *
     * @param duration the {@link Duration} object to format.
     * @return a {@link String} representing the duration of the trip in the format "hh ore mm minuti".
     */
    private String formatDuration(final Duration duration) {
        final long hours = duration.toHours();
        final long minutes = duration.toMinutes() % MINUTE_IN_HOURS;
        return hours + " ore " + minutes + " minuti";
    }

    /**
     * Calculate the average weather score for the entire route.
     *
     * @param checkpoints A list of {@link CheckpointWithMeteo} representing the points along the route
     *                    where weather conditions have been verified.
     * @return an integer representing the average weather score.
     */
    private Integer calculateMeteoScore(final List<CheckpointWithMeteo> checkpoints) {
        final int totalScore = checkpoints.stream().mapToInt(CheckpointWithMeteo::getWeatherScore).sum();
        return (int) Math.round((double) totalScore / checkpoints.size());
    }
}
