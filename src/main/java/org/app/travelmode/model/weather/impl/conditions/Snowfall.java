package org.app.travelmode.model.weather;

/**
 * Represents snowfall amounts from the preceding hour, extending the abstract weather condition base class.
 * This class handles different levels of snowfall accumulation measured in centimeters (cm) per hour and
 * assigns corresponding intensity scores.
 *
 * <p>Hourly snowfall levels are categorized as follows:
 * <ul>
 *     <li>Extreme: ≥ 3.0 cm/h (Score: 100)</li>
 *     <li>Heavy: ≥ 1.5 cm/h (Score: 70)</li>
 *     <li>Moderate: ≥ 0.5 cm/h (Score: 40)</li>
 *     <li>Light: ≥ 0.1 cm/h (Score: 20)</li>
 *     <li>None: 0.0 cm/h (Score: 0)</li>
 * </ul>
 */
public class Snowfall extends AbstractWeatherCondition {

    private static final double WEIGHT = 1.5;
    private static final String NAME = "Snowfall";

    private final SnowfallLevel intensity;

    /**
     * Constructs a new Snowfall instance based on the measured snowfall amount from the preceding hour.
     *
     * @param snowfallCm the amount of snowfall in centimeters (cm) that has fallen in the previous hour
     */
    public Snowfall(double snowfallCm) {
        super(NAME, WEIGHT);
        this.intensity = SnowfallLevel.fromValue(snowfallCm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIntensityScore() {
        return this.intensity.getIntensityScore();
    }

    /**
     * Enumeration of possible hourly snowfall levels with their corresponding
     * threshold values and intensity scores.
     */
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

        /**
         * Determines the appropriate snowfall level based on a measured hourly value.
         *
         * @param value the measured snowfall in centimeters for the preceding hour
         * @return the corresponding snowfall level, or NO_SNOWFALL if no threshold is met
         */
        public static SnowfallLevel fromValue(double value) {
            for (final SnowfallLevel snowfallLevel : SnowfallLevel.values()) {
                if (value >= snowfallLevel.intensity) {
                    return snowfallLevel;
                }
            }
            return NO_SNOWFALL;
        }

        /**
         * Gets the intensity score for this hourly snowfall level.
         *
         * @return the intensity score as an integer
         */
        public int getIntensityScore() {
            return intensityScore;
        }
    }

}
