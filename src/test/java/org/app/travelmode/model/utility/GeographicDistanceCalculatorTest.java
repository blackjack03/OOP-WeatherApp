package org.app.travelmode.model.utility;

import org.app.travelmode.model.google.dto.directions.LatLng;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link GeographicDistanceCalculator} class.
 * <p>
 * These tests verify the correctness and precision of the method {@code computeDistance(LatLng, LatLng)},
 * which calculates the great-circle distance between two geographic coordinates.
 * </p>
 *
 * <p>The tests cover different cases including:</p>
 * <ul>
 *   <li>Two identical coordinates (expected distance = 0)</li>
 *   <li>Real-world cities (Rome to Milan)</li>
 *   <li>Points across hemispheres</li>
 *   <li>Points less than one kilometer apart</li>
 * </ul>
 *
 */
class GeographicDistanceCalculatorTest {

    private static final double DIST_SAME_POINT = 0.0;
    private static final double DIST_ROME_MILAN = 477019;
    private static final double DIST_NORTH_SOUTH = 1569000;
    private static final double DIST_NEAR_POINTS = 26.0;

    private static final double DELTA_SAME_POINT = 0.001;
    private static final double DELTA_ROME_MILAN = 2000;
    private static final double DELTA_NORTH_SOUTH = 1000;
    private static final double DELTA_NEAR = 3.0;

    /**
     * Tests that the distance between a point and itself is exactly zero.
     */
    @Test
    void testComputeDistanceSamePoint() {
        final LatLng point = new LatLng(41.9028, 12.4964); // Roma
        final double distance = GeographicDistanceCalculator.computeDistance(point, point).doubleValue();
        assertEquals(DIST_SAME_POINT, distance, DELTA_SAME_POINT,
                "La distanza tra lo stesso punto dovrebbe essere 0.");
    }

    /**
     * Tests the approximate distance between Rome and Milan (~477 km),
     * allowing for a small margin of error.
     */
    @Test
    void testComputeDistanceDifferentPoints() {
        final LatLng rome = new LatLng(41.9028, 12.4964); // Roma
        final LatLng milan = new LatLng(45.4642, 9.1900); // Milano
        final double distance = GeographicDistanceCalculator.computeDistance(rome, milan).doubleValue();

        assertEquals(DIST_ROME_MILAN, distance, DELTA_ROME_MILAN,
                "La distanza tra Roma e Milano dovrebbe essere circa 477 km.");
    }

    /**
     * Tests distance calculation between two points on different hemispheres.
     */
    @Test
    void testComputeDistanceHemisphereDifference() {
        final LatLng pointNorth = new LatLng(0.0, 0.0);
        final LatLng pointSouth = new LatLng(-10.0, -10.0);
        final double distance = GeographicDistanceCalculator.computeDistance(pointNorth, pointSouth).doubleValue();

        assertEquals(DIST_NORTH_SOUTH, distance, DELTA_NORTH_SOUTH,
                "La distanza tra i due punti dovrebbe essere circa 1569 km.");
    }

    /**
     * Tests the calculation of distance between two nearby coordinates,
     * where the result is expected to be less than 1 kilometer (~26 meters).
     */
    @Test
    void testComputeDistanceLessThanOneKilometer() {
        final LatLng point1 = new LatLng(44.1484245, 12.2354429);
        final LatLng point2 = new LatLng(44.1481884, 12.2354903);

        final double distance = GeographicDistanceCalculator.computeDistance(point1, point2).doubleValue();

        assertEquals(DIST_NEAR_POINTS, distance, DELTA_NEAR,
                "La distanza tra i due punti dovrebbe essere circa 26 m.");
    }
}
