package org.app.travelmode.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.scene.image.Image;

import java.io.FileReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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
    private final MapImageGenerator mapImageGenerator;
    private Image mapImage;
    private String googleApiKey;

    public TravelModeResultImpl(final List<CheckpointWithMeteo> checkpoints, final String summary, final String polyline, final Duration duration) {
        this.checkpoints = checkpoints;
        this.arrivalTime = checkpoints.get(checkpoints.size() - 1).getArrivalDateTime().toLocalDateTime();
        this.summary = summary;
        this.duration = duration;
        this.polyline = polyline;
        //TODO: delegare
        try (FileReader jsonReader = new FileReader("src/main/resources/API-Keys.json")) {
            final Gson gson = new Gson();
            final JsonObject jsonObject = gson.fromJson(jsonReader, JsonObject.class);
            this.googleApiKey = jsonObject.get("google-api-key").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mapImageGenerator = new MapImageGeneratorImpl(googleApiKey);
    }

    @Override
    public List<CheckpointWithMeteo> getCheckpoints() {
        return List.copyOf(checkpoints);
    }

    @Override
    public Image getMapImage() {
        this.mapImage = this.mapImageGenerator.generateMapImage(checkpoints, polyline);
        return mapImage;
    }

    @Override
    public int getMeteoScore() {
        if (this.checkpoints.isEmpty()) {
            throw new IllegalArgumentException("La lista di checkpoint non può essere vuota.");
        }

        int totalScore = 0;
        for (final CheckpointWithMeteo checkpoint : this.checkpoints) {
            totalScore += checkpoint.getWeatherScore();
        }

        return (int) Math.round((double) totalScore / this.checkpoints.size());
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
}
