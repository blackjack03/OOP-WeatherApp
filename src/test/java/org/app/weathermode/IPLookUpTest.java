package org.app.weathermode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import org.app.weathermode.model.Pair;
import org.app.weathermode.model.AdvancedJsonReaderImpl;
import org.app.weathermode.model.LookUp;
import org.app.weathermode.model.IPLookUp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test JUnit 5 per {@link IPLookUp}. Tutti i test evitano traffico di rete
 * sfruttando {@link MockedConstruction} di Mockito per intercettare la
 * costruzione di {@link AdvancedJsonReaderImpl} all'interno di
 * {@link IPLookUp#doLookUpReq()}.
 */
class IPLookUpTest {

    private LookUp underTest;

    @BeforeEach
    void setUp() {
        underTest = new IPLookUp();
    }

    @Test
    void lookupShouldReturnEmptyWhenAllAttemptsFail() {
        try (final MockedConstruction<AdvancedJsonReaderImpl> mocked =
                mockConstruction(AdvancedJsonReaderImpl.class,
                (mock, ctx) -> when(mock.getString(anyString()))
                .thenThrow(new RuntimeException("fail")))) {
            final Optional<Boolean> result = underTest.lookup();
            assertTrue(result.isEmpty(),
                "lookup() deve restituire Optional.empty() se tutti i tentativi falliscono");
            // Nessun campo deve essere valorizzato
            assertAll(
                    () -> assertEquals("", underTest.getIP()),
                    () -> assertNull(underTest.getCoords())
            );
            // Deve essere stata tentata la costruzione MAX_ATTEMPTS volte
            assertEquals(10, mocked.constructed().size());
        }
    }

    @Test
    void lookupShouldPopulateFieldsOnFirstSuccessfulAttempt() {
        final String expectedIP = "8.8.8.8";
        final String expectedCountry = "Italy";
        final String expectedCity = "Rome";

        try (final MockedConstruction<AdvancedJsonReaderImpl> mocked =
                mockConstruction(AdvancedJsonReaderImpl.class, (mock, ctx) -> {
            when(mock.getString("ip")).thenReturn(expectedIP);
            when(mock.getString("country_code")).thenReturn("IT");
            when(mock.getString("country_name")).thenReturn(expectedCountry);
            when(mock.getString("region_name")).thenReturn("Lazio");
            when(mock.getString("city")).thenReturn(expectedCity);
            when(mock.getString("zip_code")).thenReturn("00100");
            when(mock.getString("time_zone")).thenReturn("Europe/Rome");
            when(mock.getDouble("latitude")).thenReturn(41.9);
            when(mock.getDouble("longitude")).thenReturn(12.5);
        })) {
            final Optional<Boolean> ok = underTest.lookup();
            assertTrue(ok.isPresent() && ok.get());

            assertAll("campi popolati",
                    () -> assertEquals(expectedIP, underTest.getIP()),
                    () -> assertEquals("IT", underTest.getCountryCode()),
                    () -> assertEquals(expectedCountry, underTest.getCountry()),
                    () -> assertEquals(expectedCity, underTest.getCity()),
                    () -> assertNotNull(underTest.getCoords()),
                    () -> assertEquals(41.9, underTest.getCoords().getX()),
                    () -> assertEquals(12.5, underTest.getCoords().getY())
            );
            // deve chiamare una sola volta il costruttore perché già al primo tentativo ha successo
            assertEquals(1, mocked.constructed().size());
        }
    }

    /* ---------------------------------------------------------- */
    /*  clear() privato via reflection                             */
    /* ---------------------------------------------------------- */

    @Test
    void clearShouldResetAllFields() throws Exception {
        // Imposta manualmente valori privati
        final Field ip = IPLookUp.class.getDeclaredField("ip");
        final Field coords = IPLookUp.class.getDeclaredField("coords");
        ip.setAccessible(true);
        coords.setAccessible(true);
        ip.set(underTest, "1.2.3.4");
        coords.set(underTest, new Pair<>(10.0, 20.0));

        // Invoca clear() via reflection
        final Method clearM = IPLookUp.class.getDeclaredMethod("clear");
        clearM.setAccessible(true);
        clearM.invoke(underTest);

        assertAll(
                () -> assertEquals("", underTest.getIP()),
                () -> assertNull(underTest.getCoords())
        );
    }

}
