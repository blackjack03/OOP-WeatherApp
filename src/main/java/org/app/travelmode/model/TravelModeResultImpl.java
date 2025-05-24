package org.app.travelmode.model;

import javafx.scene.image.Image;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link TravelModeResult} interface representing the result of the analysis of a trip,
 * including information about checkpoints, summary, duration, arrival time, polyline, and map image.
 */

public class TravelModeResultImpl implements TravelModeResult {

    private final List<CheckpointWithMeteo> checkpoints;
    private final String summary;
    private final Duration duration;
    private final LocalDateTime arrivalTime;
    private final String polyline;
    private final StaticMapApiClient mapImageGenerator;
    private Optional<Image> mapImage;
    private Optional<String> durationString;
    private Optional<Integer> meteoScore;

    public TravelModeResultImpl(final List<CheckpointWithMeteo> checkpoints, final String summary, final String polyline, final Duration duration) {
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

    @Override
    public List<CheckpointWithMeteo> getCheckpoints() {
        return List.copyOf(checkpoints);
    }

    @Override
    public Image getMapImage() {
        if (this.mapImage.isEmpty()) {
            this.mapImage = Optional.of(mapImageGenerator.generateMapImage(checkpoints, polyline));
        }
        return mapImage.get();
    }

    @Override
    public int getMeteoScore() {
        if (this.checkpoints.isEmpty()) {
            throw new IllegalArgumentException("La lista di checkpoint non può essere vuota.");
        }
        if (this.meteoScore.isEmpty()) {
            this.meteoScore = Optional.of(calculateMeteoScore(this.checkpoints));
        }
        return meteoScore.get();
    }

    @Override
    public String getSummary() {
        return this.summary;
    }

    @Override
    public Duration getDuration() {
        return this.duration;
    }

    @Override
    public LocalDateTime getArrivalTime() {
        return this.arrivalTime;
    }

    @Override
    public String getDurationString() {
        if (this.durationString.isEmpty()) {
            this.durationString = Optional.of(formatDuration(duration));
        }
        return this.durationString.get();
    }

    private String formatDuration(final Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return hours + " ore " + minutes + " minuti";
    }

    private Integer calculateMeteoScore(final List<CheckpointWithMeteo> checkpoints) {
        int totalScore = 0;
        for (final CheckpointWithMeteo checkpoint : checkpoints) {
            totalScore += checkpoint.getWeatherScore();
        }
        return (int) Math.round((double) totalScore / checkpoints.size());
    }
}
