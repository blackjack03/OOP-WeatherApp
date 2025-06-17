package org.app.travelmode.model.weather.impl.conditions;

/**
 * Represents precipitation weather conditions, extending the abstract weather condition base class.
 * This class handles different levels of precipitation measured in millimeters (mm) and
 * assigns corresponding intensity scores.
 *
 * <p>Precipitation levels are categorized as follows:
 * <ul>
 *     <li>Extreme: ≥ 8.0 mm (Score: 100)</li>
 *     <li>Heavy: ≥ 4.0 mm (Score: 70)</li>
 *     <li>Moderate: ≥ 1.0 mm (Score: 40)</li>
 *     <li>Light: ≥ 0.1 mm (Score: 20)</li>
 *     <li>None: 0.0 mm (Score: 0)</li>
 * </ul>
 */
public class Precipitation extends AbstractWeatherCondition {

    private static final String NAME = "Precipitation";
    private static final double WEIGHT = 1.3;

    private final PrecipitationLevel intensity;

    /**
     * Constructs a new Precipitation instance based on the measured precipitation amount.
     *
     * @param precipitationMm the amount of precipitation in millimeters(mm)
     */
    public Precipitation(double precipitationMm) {
        super(NAME, WEIGHT);
        this.intensity = PrecipitationLevel.fromValue(precipitationMm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIntensityScore() {
        return this.intensity.getIntensityScore();
    }

    /**
     * Enumeration of possible precipitation levels with their corresponding
     * threshold values and intensity scores.
     */
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

        /**
         * Determines the appropriate precipitation level based on a measured value.
         *
         * @param value the measured precipitation in millimeters
         * @return the corresponding precipitation level, or NO_PRECIPITATION if no threshold is met
         */
        public static PrecipitationLevel fromValue(double value) {
            for (final PrecipitationLevel precipitation : PrecipitationLevel.values()) {
                if (value >= precipitation.intensity) {
                    return precipitation;
                }
            }
            return NO_PRECIPITATION;
        }

        /**
         * Gets the intensity score for this precipitation level.
         *
         * @return the intensity score as an integer
         */
        public int getIntensityScore() {
            return intensityScore;
        }
    }
}
