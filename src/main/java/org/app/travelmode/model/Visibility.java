package org.app.travelmode.model;

public class Visibility extends AbstractWeatherCondition {

    private static final double WEIGHT = 1.6;
    private static final String NAME = "Visibility";

    private final VisibilityLevel intensity;

    public Visibility(double visibilityMeter) {
        super(NAME, WEIGHT);
        this.intensity = VisibilityLevel.fromValue(visibilityMeter);
    }

    @Override
    public int getIntensityScore() {
        return this.intensity.getIntensityScore();
    }

    private enum VisibilityLevel {
        EXCELLENT_VISIBILITY(10000.0, 0),
        GOOD_VISIBILITY(7000.0, 20),
        MODERATE_VISIBILITY(4000.0, 40),
        POOR_VISIBILITY(1000.0, 70),
        VERY_BAD_VISIBILITY(0.0, 100);

        private final double intensity;
        private final int intensityScore;

        VisibilityLevel(double intensity, int intensityScore) {
            this.intensity = intensity;
            this.intensityScore = intensityScore;
        }

        public static VisibilityLevel fromValue(double value) {
            for (final VisibilityLevel visibilityLevel : VisibilityLevel.values()) {
                if (value >= visibilityLevel.intensity) {
                    return visibilityLevel;
                }
            }
            return VERY_BAD_VISIBILITY;

        }

        public int getIntensityScore() {
            return intensityScore;
        }
    }

}
