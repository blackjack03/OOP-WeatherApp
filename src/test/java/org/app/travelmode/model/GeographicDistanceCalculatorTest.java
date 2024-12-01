package org.app.travelmode.model;

import org.app.travelmode.directions.LatLng;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeographicDistanceCalculatorTest {

    @Test
    void testComputeDistance_samePoint() {
        // Due punti identici dovrebbero avere una distanza di 0
        LatLng point = new LatLng(41.9028, 12.4964); // Roma
        double distance = GeographicDistanceCalculator.computeDistance(point, point);
        assertEquals(0.0, distance, 0.001, "La distanza tra lo stesso punto dovrebbe essere 0.");
    }

    @Test
    void testComputeDistance_differentPoints() {
        // Testare la distanza tra Roma e Milano
        LatLng rome = new LatLng(41.9028, 12.4964); // Roma
        LatLng milan = new LatLng(45.4642, 9.1900); // Milano
        double distance = GeographicDistanceCalculator.computeDistance(rome, milan);

        // Valore atteso calcolato con altri strumenti: ~477 km
        assertEquals(477, distance, 5, "La distanza tra Roma e Milano dovrebbe essere circa 477 km.");
    }

    @Test
    void testComputeDistance_hemisphereDifference() {
        // Testare la distanza tra un punto nell'emisfero nord e uno nel sud
        LatLng pointNorth = new LatLng(0.0, 0.0); // Equatore
        LatLng pointSouth = new LatLng(-10.0, -10.0); // 10° Sud, 10° Ovest
        double distance = GeographicDistanceCalculator.computeDistance(pointNorth, pointSouth);

        // Valore atteso calcolato con altri strumenti: ~1569 km
        assertEquals(1569, distance, 10, "La distanza tra i due punti dovrebbe essere circa 1569 km.");
    }

    @Test
    void testComputeDistance_lessThanOneKilometer() {
        // Due punti vicini tra loro (meno di 1 km)
        LatLng point1 = new LatLng(44.1484245, 12.2354429);
        LatLng point2 = new LatLng(44.1481884, 12.2354903);

        double distance = GeographicDistanceCalculator.computeDistance(point1, point2);

        assertEquals(0.026, distance, 0.005, "La distanza tra i due punti dovrebbe essere circa 26 m.");
    }
}