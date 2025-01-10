package org.app.travelmode.model;

import javafx.scene.image.Image;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface TravelModeResult {

    List<CheckpointWithMeteo> getCheckpoints();

    Image getMapImage();

    int getMeteoScore();

    String getSummary();

    Duration getDuration();

    LocalDateTime getArrivalTime();
}
