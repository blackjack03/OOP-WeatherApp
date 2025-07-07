package org.app.travelmode.model.utility;

import org.app.travelmode.model.google.dto.directions.LatLng;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link PolylineDecoder} class.
 * <p>
 * These tests verify that encoded polylines are correctly decoded into lists of {@link LatLng} points.
 * The tested cases include:
 * <ul>
 *   <li>Decoding a single point</li>
 *   <li>Decoding multiple points</li>
 *   <li>Handling an empty polyline string</li>
 * </ul>
 *
 * <p>Each decoded point is checked for precision within a small delta.</p>
 */
class PolylineDecoderTest {

    private static final double LAT_POINT1 = 38.5;
    private static final double LNG_POINT1 = -120.2;

    private static final double LAT_POINT2 = 40.7;
    private static final double LNG_POINT2 = -120.95;

    private static final double COORD_DELTA = 0.000_01;

    /**
     * Tests decoding of a polyline representing a single point.
     * Expected point: (38.5, -120.2)
     */
    @Test
    void testDecodeSinglePoint() {
        // Polilinea codificata per un singolo punto: 38.5, -120.2
        final String encodedPolyline = "_p~iF~ps|U";
        final List<LatLng> decodedPoints = PolylineDecoder.decode(encodedPolyline);

        assertEquals(1, decodedPoints.size(), "Dovrebbe essere decodificato un solo punto.");

        final LatLng point = decodedPoints.get(0);
        assertEquals(LAT_POINT1, point.getLat(), COORD_DELTA, "Latitudine non corrisponde.");
        assertEquals(LNG_POINT1, point.getLng(), COORD_DELTA, "Longitudine non corrisponde.");
    }

    /**
     * Tests decoding of a polyline representing multiple points.
     * Expected points:
     * <ul>
     *   <li>(38.5, -120.2)</li>
     *   <li>(40.7, -120.95)</li>
     * </ul>
     */
    @Test
    void testDecodeMultiplePoints() {
        // Polilinea codificata per due punti: (38.5, -120.2) e (40.7, -120.95)
        final String encodedPolyline = "_p~iF~ps|U_ulLnnqC";
        final List<LatLng> decodedPoints = PolylineDecoder.decode(encodedPolyline);

        assertEquals(2, decodedPoints.size(), "Dovrebbero essere decodificati due punti.");

        final LatLng point1 = decodedPoints.get(0);
        assertEquals(LAT_POINT1, point1.getLat(), COORD_DELTA, "Latitudine del primo punto non corrisponde.");
        assertEquals(LNG_POINT1, point1.getLng(), COORD_DELTA, "Longitudine del primo punto non corrisponde.");

        final LatLng point2 = decodedPoints.get(1);
        assertEquals(LAT_POINT2, point2.getLat(), COORD_DELTA, "Latitudine del secondo punto non corrisponde.");
        assertEquals(LNG_POINT2, point2.getLng(), COORD_DELTA, "Longitudine del secondo punto non corrisponde.");
    }

    /**
     * Tests decoding of an empty polyline string.
     * The returned list should be empty.
     */
    @Test
    void testDecodeEmptyPolyline() {
        final String encodedPolyline = "";
        final List<LatLng> decodedPoints = PolylineDecoder.decode(encodedPolyline);

        assertTrue(decodedPoints.isEmpty(), "La lista dei punti dovrebbe essere vuota.");
    }
}
