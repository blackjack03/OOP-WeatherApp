package org.app.travelmode.model.weather.impl.conditions;

/**
 * It represents the freezing risk conditions based on the altitude above sea level where 0°C (freezing level) is reached,
 * extending the abstract weather condition base class. This class evaluates the risk of freezing
 * conditions based on how close the freezing level is to the ground level.
 *
 * <p>The freezing risk levels are categorized based on the height of the 0°C level above sea level:
 * <ul>
 *     <li>No Risk: ≥ 1500 meters (Score: 0)</li>
 *     <li>Light Risk: ≥ 1000 meters (Score: 20)</li>
 *     <li>Moderate Risk: ≥ 500 meters (Score: 40)</li>
 *     <li>Heavy Risk: ≥ 200 meters (Score: 70)</li>
 *     <li>Extreme Risk: < 200 meters (Score: 100)</li>
 * </ul>
 */
public class FreezingRisk extends AbstractWeatherCondition {

    private static final double WEIGHT = 1.8;
    private static final String NAME = "Freezing Risk";

    private final FreezingLevel intensity;

    /**
     * Constructs a new FreezingRisk instance based on the altitude of the 0°C level.
     *
     * @param freezingHeightMetres the altitude above sea level where 0°C (freezing level) is reached
     */
    public FreezingRisk(double freezingHeightMetres) {
        super(NAME, WEIGHT);
        this.intensity = FreezingLevel.fromValue(freezingHeightMetres);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getIntensityScore() {
        return intensity.getIntensityScore();
    }

    /**
     * Enumeration of possible freezing risk levels with their corresponding
     * altitude thresholds and intensity scores.
     */
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

        /**
         * Determines the appropriate freezing risk level based on the 0°C level altitude.
         *
         * @param value the altitude of the 0°C level in meters above sea level
         * @return the corresponding freezing risk level, or EXTREME_RISK if below all thresholds
         */
        public static FreezingLevel fromValue(double value) {
            for (final FreezingLevel freezingLevel : FreezingLevel.values()) {
                if (value >= freezingLevel.intensity) {
                    return freezingLevel;
                }
            }
            return EXTREME_RISK;
        }

        /**
         * Gets the intensity score for this freezing risk level.
         *
         * @return the intensity score as an integer
         */
        public int getIntensityScore() {
            return intensityScore;
        }
    }
}
