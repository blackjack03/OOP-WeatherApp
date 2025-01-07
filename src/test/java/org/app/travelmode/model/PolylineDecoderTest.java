package org.app.travelmode.model;

import org.app.travelmode.directions.LatLng;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PolylineDecoderTest {

    @Test
    void testDecode_singlePoint() {
        // Polilinea codificata per un singolo punto: 38.5, -120.2
        String encodedPolyline = "_p~iF~ps|U";
        List<LatLng> decodedPoints = PolylineDecoder.decode(encodedPolyline);

        // Verifica che la lista abbia esattamente un punto
        assertEquals(1, decodedPoints.size(), "Dovrebbe essere decodificato un solo punto.");

        // Verifica delle coordinate
        LatLng point = decodedPoints.get(0);
        assertEquals(38.5, point.getLat(), 0.00001, "Latitudine non corrisponde.");
        assertEquals(-120.2, point.getLng(), 0.00001, "Longitudine non corrisponde.");
    }

    @Test
    void testDecode_multiplePoints() {
        // Polilinea codificata per due punti: (38.5, -120.2) e (40.7, -120.95)
        String encodedPolyline = "_p~iF~ps|U_ulLnnqC";
        List<LatLng> decodedPoints = PolylineDecoder.decode(encodedPolyline);

        // Verifica che la lista abbia due punti
        assertEquals(2, decodedPoints.size(), "Dovrebbero essere decodificati due punti.");

        // Verifica del primo punto
        LatLng point1 = decodedPoints.get(0);
        assertEquals(38.5, point1.getLat(), 0.00001, "Latitudine del primo punto non corrisponde.");
        assertEquals(-120.2, point1.getLng(), 0.00001, "Longitudine del primo punto non corrisponde.");

        // Verifica del secondo punto
        LatLng point2 = decodedPoints.get(1);
        assertEquals(40.7, point2.getLat(), 0.00001, "Latitudine del secondo punto non corrisponde.");
        assertEquals(-120.95, point2.getLng(), 0.00001, "Longitudine del secondo punto non corrisponde.");
    }

    @Test
    void testDecode_emptyPolyline() {
        // Polilinea vuota
        String encodedPolyline = "";
        List<LatLng> decodedPoints = PolylineDecoder.decode(encodedPolyline);

        // Verifica che la lista sia vuota
        assertTrue(decodedPoints.isEmpty(), "La lista dei punti dovrebbe essere vuota.");
    }
}
