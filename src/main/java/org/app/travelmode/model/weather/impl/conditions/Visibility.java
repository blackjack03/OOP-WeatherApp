package org.app.travelmode.model.weather;

/**
 * Represents visibility conditions measured in meters, extending the abstract weather condition base class.
 * This class evaluates the viewing distance and its impact on travel safety.
 *
 * <p>Visibility levels are categorized as follows:
 * <ul>
 *     <li>Excellent: ≥ 10000 meters (Score: 0)</li>
 *     <li>Good: ≥ 7000 meters (Score: 20)</li>
 *     <li>Moderate: ≥ 4000 meters (Score: 40)</li>
 *     <li>Poor: ≥ 1000 meters (Score: 70)</li>
 *     <li>Very Bad: < 1000 meters (Score: 100)</li>
 * </ul>
 */
public class Visibility extends AbstractWeatherCondition {

    private static final double WEIGHT = 1.6;
    private static final String NAME = "Visibility";

    private final VisibilityLevel intensity;

    /**
     * Constructs a new Visibility instance based on the measured viewing distance.
     *
     * @param visibilityMeter the viewing distance in meters
     */
    public Visibility(double visibilityMeter) {
        super(NAME, WEIGHT);
        this.intensity = VisibilityLevel.fromValue(visibilityMeter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIntensityScore() {
        return this.intensity.getIntensityScore();
    }

    /**
     * Enumeration of possible visibility levels with their corresponding
     * distance thresholds and intensity scores.
     */
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

        /**
         * Determines the appropriate visibility level based on the measured distance.
         *
         * @param value the measured visibility distance in meters
         * @return the corresponding visibility level, or VERY_BAD_VISIBILITY if below all thresholds
         */
        public static VisibilityLevel fromValue(double value) {
            for (final VisibilityLevel visibilityLevel : VisibilityLevel.values()) {
                if (value >= visibilityLevel.intensity) {
                    return visibilityLevel;
                }
            }
            return VERY_BAD_VISIBILITY;

        }

        /**
         * Gets the intensity score for this visibility level.
         *
         * @return the intensity score as an integer
         */
        public int getIntensityScore() {
            return intensityScore;
        }
    }

}
