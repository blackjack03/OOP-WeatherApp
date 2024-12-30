package org.app.travelmode.model;

import javafx.scene.image.Image;

import java.util.List;

public class TravelModeResultImpl implements TravelModeResult{

    private final List<CheckpointWithMeteo> checkpoints;
    private final String summary;
    private Image mapImage;

    public TravelModeResultImpl(final List<CheckpointWithMeteo> checkpoints, final String summary) {
        this.checkpoints = checkpoints;
        this.summary = summary;
    }

    @Override
    public List<CheckpointWithMeteo> getCheckpoints() {
        return List.copyOf(checkpoints);
    }

    @Override
    public Image getMapImage() {
        return this.mapImage;
    }

    @Override
    public int getMeteoScore() {
        return 0;
    }

    @Override
    public String getSummary() {
        return this.summary;
    }
}
