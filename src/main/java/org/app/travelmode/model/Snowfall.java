package org.app.travelmode.model;

public class Snowfall extends AbstractWeatherCondition {

    private static final double WEIGHT = 1.5;
    private static final String NAME = "Snowfall";

    private final SnowfallLevel intensity;

    public Snowfall(double snowfallCm) {
        super(NAME, WEIGHT);
        this.intensity = SnowfallLevel.fromValue(snowfallCm);
    }

    @Override
    public int getIntensityScore() {
        return this.intensity.getIntensityScore();
    }

    private enum SnowfallLevel {
        EXTREME_SNOWFALL(3.0, 100),
        HEAVY_SNOWFALL(1.5, 70),
        MODERATE_SNOWFALL(0.5, 40),
        LIGHT_SNOWFALL(0.1, 20),
        NO_SNOWFALL(0.0, 0);

        private final double intensity;
        private final int intensityScore;

        SnowfallLevel(double intensity, int intensityScore) {
            this.intensity = intensity;
            this.intensityScore = intensityScore;
        }

        public static SnowfallLevel fromValue(double value) {
            for (final SnowfallLevel snowfallLevel : SnowfallLevel.values()) {
                if (value >= snowfallLevel.intensity) {
                    return snowfallLevel;
                }
            }
            return NO_SNOWFALL;
        }

        public int getIntensityScore() {
            return intensityScore;
        }
    }

}
