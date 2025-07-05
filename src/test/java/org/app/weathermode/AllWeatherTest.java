package org.app.weathermode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.app.weathermode.model.AllWeather;

// CHECKSTYLE: AvoidStarImport OFF
import static org.junit.jupiter.api.Assertions.*;
// CHECKSTYLE: AvoidStarImport ON

/**
 * Unit‑tests di base per {@link AllWeather} che non richiedono chiamate HTTP
 * né scraping live. I metodi privati vengono testati via reflection poiché la
 * classe non espone factories/mocking‑point esterni.
 */
class AllWeatherTest {

    private AllWeather underTest;

    /**
     * Costruisce un'istanza "dummy" con coordinate di Roma.
     */
    @BeforeEach
    void setUp() {
        final Map<String, String> loc = new HashMap<>();
        loc.put("city", "Roma");
        loc.put("city_ascii", "Roma");
        loc.put("lat", "41.894741");
        loc.put("lng", "12.481100");
        underTest = new AllWeather(loc);
    }

    /* ---------------------------------------------------------- */
    /*  API pubbliche                                             */
    /* ---------------------------------------------------------- */

    @Test
    void windDirectionShouldReturnExpectedItalianCompassNames() {
        // CHECKSTYLE: MagicNumber OFF
        assertAll("direzioni bussola",
                () -> assertEquals("Nord",     underTest.getWindDirection(0)),
                () -> assertEquals("Nord",     underTest.getWindDirection(360)),
                () -> assertEquals("Nordest",  underTest.getWindDirection(45)),
                () -> assertEquals("Est",      underTest.getWindDirection(90)),
                () -> assertEquals("Sud",      underTest.getWindDirection(180)),
                () -> assertEquals("Ovest",    underTest.getWindDirection(270)),
                () -> assertEquals("Est",      underTest.getWindDirection(-270)) // normalizzazione valori negativi
        );
        // CHECKSTYLE: MagicNumber ON
    }

    @Test
    void setLocationShouldResetInternalCaches() {
        final Map<String, String> milano = Map.of(
                "city", "Milano",
                "city_ascii", "Milano",
                "lat", "45.464203",
                "lng", "9.189982"
        );

        underTest.setLocation(milano);

        assertEquals(0, underTest.getForecastDays());
        assertTrue(underTest.getAllForecast().isEmpty());
        assertTrue(underTest.getDailyGeneralForecast().isEmpty());
        assertTrue(underTest.getDailyInfo().isEmpty());
    }

    /* ---------------------------------------------------------- */
    /*  Metodi privati (reflection)                               */
    /* ---------------------------------------------------------- */

    @Test
    void roundToNearestQuarterShouldRoundCorrectly() {
        assertDoesNotThrow(() -> {
            final Method round = AllWeather.class
                    .getDeclaredMethod("roundToNearestQuarter", String.class);
            round.setAccessible(true);

            assertAll("arrotondamento 15′",
                () -> assertEquals("13:30", round.invoke(underTest, "13:34")),
                () -> assertEquals("13:30", round.invoke(underTest, "13:37")),
                () -> assertEquals("14:00", round.invoke(underTest, "13:53")),
                () -> assertEquals("00:00", round.invoke(underTest, "23:59"))
            );
        });
    }

    @Test
    void checkMinutesPassedShouldRespectThreshold() {
        assertDoesNotThrow(() -> {
            final Method m = AllWeather.class.getDeclaredMethod(
                    "checkMinutesPassed", long.class, int.class);
            m.setAccessible(true);

            // CHECKSTYLE: MagicNumber OFF
            final long now = System.currentTimeMillis() / 1000L;
            final long tenMinutesAgo = now - 10 * 60;
            final long thirtyMinutesAgo = now - 30 * 60;

            assertFalse((Boolean) m.invoke(underTest, tenMinutesAgo, 20));
            assertTrue((Boolean) m.invoke(underTest, thirtyMinutesAgo, 20));
            // CHECKSTYLE: MagicNumber ON
        });
    }

}
