package org.app.travelmode.model;

public enum Intensity {
    LOW(0.5),
    MEDIUM(1.0),
    HIGH(1.5);

    private final double multiplier;

    Intensity(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
