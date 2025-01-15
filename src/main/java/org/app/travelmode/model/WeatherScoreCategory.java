package org.app.travelmode.model;

public enum WeatherScoreCategory {

    EXCELLENT(76, 100),
    GOOD(51, 75),
    BAD(26, 50),
    TERRIBLE(0, 25);

    private final int minScore;
    private final int maxScore;

    WeatherScoreCategory(int minScore, int maxScore) {
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public int getMinScore() {
        return minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    /**
     * Determines the category corresponding to a specific score.
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
