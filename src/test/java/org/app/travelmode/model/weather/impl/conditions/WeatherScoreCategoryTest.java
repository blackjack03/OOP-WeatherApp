package org.app.travelmode.model;

import org.app.travelmode.model.weather.impl.conditions.WeatherScoreCategory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class WeatherScoreCategoryTest {

    @Test
    void testCategoryRangeValues() {
        assertEquals(76, WeatherScoreCategory.EXCELLENT.getMinScore(), "Min score di EXCELLENT errato");
        assertEquals(100, WeatherScoreCategory.EXCELLENT.getMaxScore(), "Max score di EXCELLENT errato");

        assertEquals(51, WeatherScoreCategory.GOOD.getMinScore(), "Min score di GOOD errato");
        assertEquals(75, WeatherScoreCategory.GOOD.getMaxScore(), "Max score di GOOD errato");

        assertEquals(26, WeatherScoreCategory.BAD.getMinScore(), "Min score di BAD errato");
        assertEquals(50, WeatherScoreCategory.BAD.getMaxScore(), "Max score di BAD errato");

        assertEquals(0, WeatherScoreCategory.TERRIBLE.getMinScore(), "Min score di TERRIBLE errato");
        assertEquals(25, WeatherScoreCategory.TERRIBLE.getMaxScore(), "Max score di TERRIBLE errato");
    }

    @Test
    void testFromScoreWithValidScores() {
        assertEquals(WeatherScoreCategory.TERRIBLE, WeatherScoreCategory.fromScore(0), "Score 0 dovrebbe essere TERRIBLE");
        assertEquals(WeatherScoreCategory.TERRIBLE, WeatherScoreCategory.fromScore(12), "Score 12 dovrebbe essere TERRIBLE");
        assertEquals(WeatherScoreCategory.TERRIBLE, WeatherScoreCategory.fromScore(25), "Score 25 dovrebbe essere TERRIBLE");

        assertEquals(WeatherScoreCategory.BAD, WeatherScoreCategory.fromScore(26), "Score 26 dovrebbe essere BAD");
        assertEquals(WeatherScoreCategory.BAD, WeatherScoreCategory.fromScore(38), "Score 38 dovrebbe essere BAD");
        assertEquals(WeatherScoreCategory.BAD, WeatherScoreCategory.fromScore(50), "Score 50 dovrebbe essere BAD");

        assertEquals(WeatherScoreCategory.GOOD, WeatherScoreCategory.fromScore(51), "Score 51 dovrebbe essere GOOD");
        assertEquals(WeatherScoreCategory.GOOD, WeatherScoreCategory.fromScore(63), "Score 63 dovrebbe essere GOOD");
        assertEquals(WeatherScoreCategory.GOOD, WeatherScoreCategory.fromScore(75), "Score 75 dovrebbe essere GOOD");

        assertEquals(WeatherScoreCategory.EXCELLENT, WeatherScoreCategory.fromScore(76), "Score 76 dovrebbe essere EXCELLENT");
        assertEquals(WeatherScoreCategory.EXCELLENT, WeatherScoreCategory.fromScore(88), "Score 88 dovrebbe essere EXCELLENT");
        assertEquals(WeatherScoreCategory.EXCELLENT, WeatherScoreCategory.fromScore(100), "Score 100 dovrebbe essere EXCELLENT");
    }

    @Test
    void testFromScoreWithInvalidScores() {
        final Exception exceptionNegative = assertThrows(IllegalArgumentException.class, () -> WeatherScoreCategory.fromScore(-1),
                "Doveva essere lanciata un'eccezione per score -1");

        assertEquals("Il punteggio deve essere compreso tra 0 e 100", exceptionNegative.getMessage(),
                "Messaggio d'errore errato per score negativo");

        final Exception exceptionTooHigh = assertThrows(IllegalArgumentException.class, () -> WeatherScoreCategory.fromScore(101),
                "Doveva essere lanciata un'eccezione per score 101");

        assertEquals("Il punteggio deve essere compreso tra 0 e 100", exceptionTooHigh.getMessage(),
                "Messaggio d'errore errato per score 101");
    }

    @Test
    void testBoundaryValues() {
        assertEquals(WeatherScoreCategory.TERRIBLE, WeatherScoreCategory.fromScore(0), "Score 0 dovrebbe essere TERRIBLE");
        assertEquals(WeatherScoreCategory.BAD, WeatherScoreCategory.fromScore(26), "Score 26 dovrebbe essere BAD");
        assertEquals(WeatherScoreCategory.GOOD, WeatherScoreCategory.fromScore(51), "Score 51 dovrebbe essere GOOD");
        assertEquals(WeatherScoreCategory.EXCELLENT, WeatherScoreCategory.fromScore(76), "Score 76 dovrebbe essere EXCELLENT");

        assertEquals(WeatherScoreCategory.TERRIBLE, WeatherScoreCategory.fromScore(25), "Score 25 dovrebbe essere TERRIBLE");
        assertEquals(WeatherScoreCategory.BAD, WeatherScoreCategory.fromScore(50), "Score 50 dovrebbe essere BAD");
        assertEquals(WeatherScoreCategory.GOOD, WeatherScoreCategory.fromScore(75), "Score 75 dovrebbe essere GOOD");
        assertEquals(WeatherScoreCategory.EXCELLENT, WeatherScoreCategory.fromScore(100), "Score 100 dovrebbe essere EXCELLENT");
    }

}
