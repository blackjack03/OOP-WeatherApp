package org.app.travelmode.model;

public class WindGust extends AbstractWeatherCondition {

    private static final double WEIGHT = 1.6;
    private static final String NAME = "WindGust";

    private final WindGustLevel intensity;

    public WindGust(double windGustSpeed) {
        super(NAME, WEIGHT);
        this.intensity = WindGustLevel.fromValue(windGustSpeed);
    }

    @Override
    public int getIntensityScore() {
        return this.intensity.getIntensityScore();
    }

    private enum WindGustLevel {
        EXTREME_WIND_GUST(40.0, 100),
        HEAVY_WIND_GUST(30.0, 70),
        MODERATE_WIND_GUST(20.0, 40),
        LIGHT_WIND_GUST(10.0, 20),
        NO_WIND_GUST(0.0, 0);

        private final double intensity;
        private final int intensityScore;

        WindGustLevel(double intensity, int intensityScore) {
            this.intensity = intensity;
            this.intensityScore = intensityScore;
        }

        public static WindGustLevel fromValue(double value) {
            for (final WindGustLevel windGustLevel : WindGustLevel.values()) {
                if (value >= windGustLevel.intensity) {
                    return windGustLevel;
                }
            }
            return NO_WIND_GUST;

        }

        public int getIntensityScore() {
            return intensityScore;
        }
    }
}
