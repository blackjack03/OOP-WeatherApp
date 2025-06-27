package org.app.weathermode;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.Optional;

import org.app.weathermode.model.LocationSelector;
import org.app.weathermode.model.LocationSelectorImpl;

public class LocationSelectorTest {
    
    private final LocationSelector locationSelector = new LocationSelectorImpl();

    @Test
    public void testGetLocationTrue() {
        final int CITY_ID = 1840034016;
        final Optional<Map<String, String>> location = locationSelector.getByID(CITY_ID);
        assertTrue(location.isPresent());
        final Map<String, String> city = location.get();
        assertEquals(city.get("city"), "New York");
    }

    @Test
    public void testGetLocationNotPresent() {
        final int CITY_ID = 0;
        final Optional<Map<String, String>> location = locationSelector.getByID(CITY_ID);
        assertTrue(location.isEmpty());
    }

    @Test
    public void testGetLocationFalse() {
        final int CITY_ID = 1840034016;
        final Optional<Map<String, String>> location = locationSelector.getByID(CITY_ID);
        assertTrue(location.isPresent());
        final Map<String, String> city = location.get();
        assertNotEquals(city.get("city"), "Seoul");
    }

}
