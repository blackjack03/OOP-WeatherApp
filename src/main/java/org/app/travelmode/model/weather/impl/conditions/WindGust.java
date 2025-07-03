package org.app.travelmode.model.weather.impl.conditions;

/**
 * Represents wind gust conditions measured at 10 meters above ground level,
 * extending the abstract weather condition base class.
 * The measurements indicate the maximum wind gust speed recorded during the preceding hour.
 *
 * <p>Wind gust levels are categorized as follows:
 * <ul>
 *     <li>Extreme: ≥ 40.0 km/h (Score: 100)</li>
 *     <li>Heavy: ≥ 30.0 km/h (Score: 70)</li>
 *     <li>Moderate: ≥ 20.0 km/h (Score: 40)</li>
 *     <li>Light: ≥ 10.0 km/h (Score: 20)</li>
 *     <li>None: < 10.0 km/h (Score: 0)</li>
 * </ul>
 */
public class WindGust extends AbstractWeatherCondition {

    private static final double WEIGHT = 1.6;
    private static final String NAME = "WindGust";

    private final WindGustLevel intensity;

    /**
     * Constructs a new WindGust instance based on the maximum gust speed from the preceding hour.
     *
     * @param windGustSpeed the maximum wind gust speed in kilometers per hour (km/h) recorded at 10 m above ground
     *                      during the preceding hour
     */
    public WindGust(final double windGustSpeed) {
        super(NAME, WEIGHT);
        this.intensity = WindGustLevel.fromValue(windGustSpeed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIntensityScore() {
        return this.intensity.getIntensityScore();
    }

    /**
     * Enumeration of possible wind gust levels with their corresponding
     * speed thresholds and intensity scores. All measurements are taken
     * at 10 meters above ground level.
     */
    private enum WindGustLevel {
        EXTREME_WIND_GUST(40.0, 100),
        HEAVY_WIND_GUST(30.0, 70),
        MODERATE_WIND_GUST(20.0, 40),
        LIGHT_WIND_GUST(10.0, 20),
        NO_WIND_GUST(0.0, 0);

        private final double intensity;
        private final int intensityScore;

        WindGustLevel(final double intensity, final int intensityScore) {
            this.intensity = intensity;
            this.intensityScore = intensityScore;
        }

        /**
         * Determines the appropriate wind gust level based on the maximum recorded speed.
         *
         * @param value the maximum wind gust speed in km/h from the preceding hour
         * @return the corresponding wind gust level, or NO_WIND_GUST if below all thresholds
         */
        public static WindGustLevel fromValue(final double value) {
            for (final WindGustLevel windGustLevel : WindGustLevel.values()) {
                if (value >= windGustLevel.intensity) {
                    return windGustLevel;
                }
            }
            return NO_WIND_GUST;

        }

        /**
         * Gets the intensity score for this wind gust level.
         *
         * @return the intensity score as an integer
         */
        public int getIntensityScore() {
            return intensityScore;
        }
    }
}
