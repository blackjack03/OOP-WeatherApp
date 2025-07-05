package org.app.weathermode;

import org.junit.jupiter.api.Test;

// CHECKSTYLE: AvoidStarImport OFF
import static org.junit.jupiter.api.Assertions.*;
// CHECKSTYLE: AvoidStarImport ON

import java.util.Map;
import java.util.Optional;

import org.app.weathermode.model.LocationSelector;
import org.app.weathermode.model.LocationSelectorImpl;

class LocationSelectorTest {

    private final LocationSelector locationSelector = new LocationSelectorImpl();

    @Test
    public void testGetLocationTrue() {
        final int cityID = 1840034016;
        final Optional<Map<String, String>> location = locationSelector.getByID(cityID);
        assertTrue(location.isPresent());
        final Map<String, String> city = location.get();
        assertEquals(city.get("city"), "New York");
    }

    @Test
    public void testGetLocationNotPresent() {
        final int cityID = 0;
        final Optional<Map<String, String>> location = locationSelector.getByID(cityID);
        assertTrue(location.isEmpty());
    }

    @Test
    public void testGetLocationFalse() {
        final int cityID = 1840034016;
        final Optional<Map<String, String>> location = locationSelector.getByID(cityID);
        assertTrue(location.isPresent());
        final Map<String, String> city = location.get();
        assertNotEquals(city.get("city"), "Seoul");
    }

}
