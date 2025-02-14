package org.app.travelmode.model;

public class FreezingRisk extends AbstractWeatherCondition {

    private static final double WEIGHT = 1.8;
    private static final String NAME = "Freezing Risk";

    private final FreezingLevel intensity;

    public FreezingRisk(double freezingHeightMetres) {
        super(NAME, WEIGHT);
        this.intensity = FreezingLevel.fromValue(freezingHeightMetres);
    }

    @Override
    public int getIntensityScore() {
        return intensity.getIntensityScore();
    }

    private enum FreezingLevel {
        NO_RISK(1500.0, 0),
        LIGHT_RISK(1000.0, 20),
        MODERATE_RISK(500.0, 40),
        HEAVY_RISK(200, 70),
        EXTREME_RISK(0, 100);

        private final double intensity;
        private final int intensityScore;

        FreezingLevel(double intensity, int intensityScore) {
            this.intensity = intensity;
            this.intensityScore = intensityScore;
        }

        public static FreezingLevel fromValue(double value) {
            for (final FreezingLevel freezingLevel : FreezingLevel.values()) {
                if (value >= freezingLevel.intensity) {
                    return freezingLevel;
                }
            }
            return EXTREME_RISK;
        }

        public int getIntensityScore() {
            return intensityScore;
        }
    }
}
