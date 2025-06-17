package org.app.travelmode.model.utility;

import org.app.travelmode.model.google.dto.directions.LatLng;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeographicDistanceCalculatorTest {

    @Test
    void testComputeDistance_samePoint() {
        // Due punti identici dovrebbero avere una distanza di 0
        final LatLng point = new LatLng(41.9028, 12.4964); // Roma
        double distance = GeographicDistanceCalculator.computeDistance(point, point).doubleValue();
        assertEquals(0.0, distance, 0.001, "La distanza tra lo stesso punto dovrebbe essere 0.");
    }

    @Test
    void testComputeDistance_differentPoints() {
        // Testare la distanza tra Roma e Milano
        final LatLng rome = new LatLng(41.9028, 12.4964); // Roma
        final LatLng milan = new LatLng(45.4642, 9.1900); // Milano
        double distance = GeographicDistanceCalculator.computeDistance(rome, milan).doubleValue();

        // Valore atteso calcolato con altri strumenti: ~477 km
        assertEquals(477019, distance, 2000, "La distanza tra Roma e Milano dovrebbe essere circa 477 km.");
    }

    @Test
    void testComputeDistance_hemisphereDifference() {
        // Testare la distanza tra un punto nell'emisfero nord e uno nel sud
        final LatLng pointNorth = new LatLng(0.0, 0.0); // Equatore
        final LatLng pointSouth = new LatLng(-10.0, -10.0); // 10° Sud, 10° Ovest
        double distance = GeographicDistanceCalculator.computeDistance(pointNorth, pointSouth).doubleValue();

        // Valore atteso calcolato con altri strumenti: ~1569 km
        assertEquals(1569000, distance, 1000, "La distanza tra i due punti dovrebbe essere circa 1569 km.");
    }

    @Test
    void testComputeDistance_lessThanOneKilometer() {
        // Due punti vicini tra loro (meno di 1 km)
        final LatLng point1 = new LatLng(44.1484245, 12.2354429);
        final LatLng point2 = new LatLng(44.1481884, 12.2354903);

        double distance = GeographicDistanceCalculator.computeDistance(point1, point2).doubleValue();

        assertEquals(26.0, distance, 3, "La distanza tra i due punti dovrebbe essere circa 26 m.");
    }
}