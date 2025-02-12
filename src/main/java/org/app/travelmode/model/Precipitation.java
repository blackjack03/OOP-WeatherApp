package org.app.travelmode.model;

public class Precipitation extends AbstractWeatherCondition {

    private static final String NAME = "Precipitation";
    private static final double WEIGHT = 1.3;

    public Precipitation(double precipitationMm) {
        super(NAME, PrecipitationLevel.fromValue(precipitationMm).getIntensityScore());
    }

    @Override
    public double getWeightedIntensityScore() {
        return getIntensityScore() * WEIGHT;
    }

    private enum PrecipitationLevel {
        EXTREME_PRECIPITATIONS(8.0, 100),
        HEAVY_PRECIPITATIONS(4.0, 70),
        MODERATE_PRECIPITATION(1.0, 40),
        LIGHT_PRECIPITATION(0.1, 20),
        NO_PRECIPITATION(0.0, 0);

        private final double intensity;
        private final int intensityScore;

        PrecipitationLevel(double intensity, int intensityScore) {
            this.intensity = intensity;
            this.intensityScore = intensityScore;
        }

        public static PrecipitationLevel fromValue(double value) {
            for (final PrecipitationLevel precipitation : PrecipitationLevel.values()) {
                if (value >= precipitation.intensity) {
                    return precipitation;
                }
            }
            return NO_PRECIPITATION;
        }

        public int getIntensityScore() {
            return intensityScore;
        }
    }
}
