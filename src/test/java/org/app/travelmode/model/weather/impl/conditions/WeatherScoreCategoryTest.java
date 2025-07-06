package org.app.travelmode.model.weather.impl.conditions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for the {@link WeatherScoreCategory} enum.
 * <p>
 * This class verifies the correct behavior of:
 * <ul>
 *   <li>Score range definitions for each category</li>
 *   <li>Mapping from integer scores to the correct category via {@link WeatherScoreCategory#fromScore(int)}</li>
 *   <li>Exception handling for invalid score inputs</li>
 *   <li>Boundary values at category transitions</li>
 * </ul>
 *
 */
class WeatherScoreCategoryTest {

    private static final int SCORE_MIN_TERRIBLE = 0;
    private static final int SCORE_MAX_TERRIBLE = 25;
    private static final int SCORE_MIN_BAD = 26;
    private static final int SCORE_MAX_BAD = 50;
    private static final int SCORE_MIN_GOOD = 51;
    private static final int SCORE_MAX_GOOD = 75;
    private static final int SCORE_MIN_EXCELLENT = 76;
    private static final int SCORE_MAX_EXCELLENT = 100;

    // Mid‑range representative scores
    private static final int SAMPLE_TERRIBLE = 12;
    private static final int SAMPLE_BAD = 38;
    private static final int SAMPLE_GOOD = 63;
    private static final int SAMPLE_EXCELLENT = 88;

    // Invalid scores for negative / overflow checks
    private static final int INVALID_SCORE_LOW = -1;
    private static final int INVALID_SCORE_HIGH = 101;

    private static final String SCORE_OUT_OF_RANGE_MESSAGE = "Il punteggio deve essere compreso tra 0 e 100";


    /**
     * Verifies that each {@link WeatherScoreCategory} has the correct min and max score values.
     */
    @Test
    void testCategoryRangeValues() {
        assertEquals(SCORE_MIN_EXCELLENT, WeatherScoreCategory.EXCELLENT.getMinScore(),
                "Min score di EXCELLENT errato");
        assertEquals(SCORE_MAX_EXCELLENT, WeatherScoreCategory.EXCELLENT.getMaxScore(),
                "Max score di EXCELLENT errato");

        assertEquals(SCORE_MIN_GOOD, WeatherScoreCategory.GOOD.getMinScore(),
                "Min score di GOOD errato");
        assertEquals(SCORE_MAX_GOOD, WeatherScoreCategory.GOOD.getMaxScore(),
                "Max score di GOOD errato");

        assertEquals(SCORE_MIN_BAD, WeatherScoreCategory.BAD.getMinScore(),
                "Min score di BAD errato");
        assertEquals(SCORE_MAX_BAD, WeatherScoreCategory.BAD.getMaxScore(),
                "Max score di BAD errato");

        assertEquals(SCORE_MIN_TERRIBLE, WeatherScoreCategory.TERRIBLE.getMinScore(),
                "Min score di TERRIBLE errato");
        assertEquals(SCORE_MAX_TERRIBLE, WeatherScoreCategory.TERRIBLE.getMaxScore(),
                "Max score di TERRIBLE errato");
    }

    /**
     * Tests that {@link WeatherScoreCategory#fromScore(int)} correctly returns the expected category
     * for various valid scores within the 0–100 range.
     */
    @Test
    void testFromScoreWithValidScores() {
        assertEquals(WeatherScoreCategory.TERRIBLE, WeatherScoreCategory.fromScore(SCORE_MIN_TERRIBLE),
                "Score 0 dovrebbe essere TERRIBLE");
        assertEquals(WeatherScoreCategory.TERRIBLE, WeatherScoreCategory.fromScore(SAMPLE_TERRIBLE),
                "Score 12 dovrebbe essere TERRIBLE");
        assertEquals(WeatherScoreCategory.TERRIBLE, WeatherScoreCategory.fromScore(SCORE_MAX_TERRIBLE),
                "Score 25 dovrebbe essere TERRIBLE");

        assertEquals(WeatherScoreCategory.BAD, WeatherScoreCategory.fromScore(SCORE_MIN_BAD),
                "Score 26 dovrebbe essere BAD");
        assertEquals(WeatherScoreCategory.BAD, WeatherScoreCategory.fromScore(SAMPLE_BAD),
                "Score 38 dovrebbe essere BAD");
        assertEquals(WeatherScoreCategory.BAD, WeatherScoreCategory.fromScore(SCORE_MAX_BAD),
                "Score 50 dovrebbe essere BAD");

        assertEquals(WeatherScoreCategory.GOOD, WeatherScoreCategory.fromScore(SCORE_MIN_GOOD),
                "Score 51 dovrebbe essere GOOD");
        assertEquals(WeatherScoreCategory.GOOD, WeatherScoreCategory.fromScore(SAMPLE_GOOD),
                "Score 63 dovrebbe essere GOOD");
        assertEquals(WeatherScoreCategory.GOOD, WeatherScoreCategory.fromScore(SCORE_MAX_GOOD),
                "Score 75 dovrebbe essere GOOD");

        assertEquals(WeatherScoreCategory.EXCELLENT, WeatherScoreCategory.fromScore(SCORE_MIN_EXCELLENT),
                "Score 76 dovrebbe essere EXCELLENT");
        assertEquals(WeatherScoreCategory.EXCELLENT, WeatherScoreCategory.fromScore(SAMPLE_EXCELLENT),
                "Score 88 dovrebbe essere EXCELLENT");
        assertEquals(WeatherScoreCategory.EXCELLENT, WeatherScoreCategory.fromScore(SCORE_MAX_EXCELLENT),
                "Score 100 dovrebbe essere EXCELLENT");
    }

    /**
     * Tests that {@link WeatherScoreCategory#fromScore(int)} throws an {@link IllegalArgumentException}
     * for scores outside the valid range (less than 0 or greater than 100).
     */
    @Test
    void testFromScoreWithInvalidScores() {
        final Exception exceptionNegative = assertThrows(IllegalArgumentException.class,
                () -> WeatherScoreCategory.fromScore(INVALID_SCORE_LOW),
                "Doveva essere lanciata un'eccezione per score -1");

        assertEquals(SCORE_OUT_OF_RANGE_MESSAGE, exceptionNegative.getMessage(),
                "Messaggio d'errore errato per score negativo");

        final Exception exceptionTooHigh = assertThrows(IllegalArgumentException.class,
                () -> WeatherScoreCategory.fromScore(INVALID_SCORE_HIGH),
                "Doveva essere lanciata un'eccezione per score 101");

        assertEquals(SCORE_OUT_OF_RANGE_MESSAGE, exceptionTooHigh.getMessage(),
                "Messaggio d'errore errato per score 101");
    }

    /**
     * Tests that the boundary scores at the edges of each category
     * correctly map to their respective {@link WeatherScoreCategory} values.
     */
    @Test
    void testBoundaryValues() {
        assertEquals(WeatherScoreCategory.TERRIBLE, WeatherScoreCategory.fromScore(SCORE_MIN_TERRIBLE),
                "Score 0 dovrebbe essere TERRIBLE");
        assertEquals(WeatherScoreCategory.BAD, WeatherScoreCategory.fromScore(SCORE_MIN_BAD),
                "Score 26 dovrebbe essere BAD");
        assertEquals(WeatherScoreCategory.GOOD, WeatherScoreCategory.fromScore(SCORE_MIN_GOOD),
                "Score 51 dovrebbe essere GOOD");
        assertEquals(WeatherScoreCategory.EXCELLENT, WeatherScoreCategory.fromScore(SCORE_MIN_EXCELLENT),
                "Score 76 dovrebbe essere EXCELLENT");

        assertEquals(WeatherScoreCategory.TERRIBLE, WeatherScoreCategory.fromScore(SCORE_MAX_TERRIBLE),
                "Score 25 dovrebbe essere TERRIBLE");
        assertEquals(WeatherScoreCategory.BAD, WeatherScoreCategory.fromScore(SCORE_MAX_BAD),
                "Score 50 dovrebbe essere BAD");
        assertEquals(WeatherScoreCategory.GOOD, WeatherScoreCategory.fromScore(SCORE_MAX_GOOD),
                "Score 75 dovrebbe essere GOOD");
        assertEquals(WeatherScoreCategory.EXCELLENT, WeatherScoreCategory.fromScore(SCORE_MAX_EXCELLENT),
                "Score 100 dovrebbe essere EXCELLENT");
    }

}
