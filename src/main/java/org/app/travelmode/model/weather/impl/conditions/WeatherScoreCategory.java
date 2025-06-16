package org.app.travelmode.model.weather;

/**
 * Represents weather score categories used to classify weather conditions based on numerical scores.
 * Each category defines a specific range of scores from 0 to 100.
 *
 * <p>The categories are defined as follows:
 * <ul>
 *     <li>{@code EXCELLENT}: 76-100 - Optimal weather conditions</li>
 *     <li>{@code GOOD}: 51-75 - Favorable weather conditions</li>
 *     <li>{@code BAD}: 26-50 - Unfavorable weather conditions</li>
 *     <li>{@code TERRIBLE}: 0-25 - Severe weather conditions</li>
 * </ul>
 */
public enum WeatherScoreCategory {

    EXCELLENT(76, 100),
    GOOD(51, 75),
    BAD(26, 50),
    TERRIBLE(0, 25);

    private final int minScore;
    private final int maxScore;

    /**
     * Constructs a weather score category with specified score range.
     *
     * @param minScore the minimum score for this category (inclusive)
     * @param maxScore the maximum score for this category (inclusive)
     */
    WeatherScoreCategory(int minScore, int maxScore) {
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    /**
     * Returns the minimum score for this category.
     *
     * @return the minimum score (inclusive)
     */
    public int getMinScore() {
        return minScore;
    }

    /**
     * Returns the maximum score for this category.
     *
     * @return the maximum score (inclusive)
     */
    public int getMaxScore() {
        return maxScore;
    }

    /**
     * Determines the appropriate weather category for a given score.
     * The score must be between 0 and 100 inclusive.
     *
     * @param score the score to be evaluated
     * @return the corresponding category
     * @throws IllegalArgumentException if the score is outside the range 0-100
     */
    public static WeatherScoreCategory fromScore(int score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("Il punteggio deve essere compreso tra 0 e 100");
        }
        for (final WeatherScoreCategory category : values()) {
            if (score >= category.minScore && score <= category.maxScore) {
                return category;
            }
        }
        throw new IllegalStateException("Intervalli non corretti nell'enum WeatherScoreCategory");
    }
}
