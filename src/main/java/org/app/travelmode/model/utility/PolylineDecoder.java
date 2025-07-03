package org.app.travelmode.model.utility;

import org.app.travelmode.model.google.dto.directions.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for decoding Google's encoded polyline format into a series of coordinates.
 *
 * <p>This decoder:
 * <ul>
 *     <li>Implements Google's polyline encoding algorithm</li>
 *     <li>Converts a compressed string format to geographic coordinates</li>
 *     <li>Handles latitude and longitude with 5 decimal places precision</li>
 * </ul>
 */
public final class PolylineDecoder {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private PolylineDecoder() {
    }

    /**
     * Decodes a polyline into a list of coordinates.
     *
     * @param encodedPolyline The Google-encoded polyline.
     * @return A list of {@link LatLng} representing the points on the polyline.
     */
    // CHECKSTYLE: MagicNumber OFF
    public static List<LatLng> decode(final String encodedPolyline) {
        final List<LatLng> polyline = new ArrayList<>();
        int index = 0;
        final int len = encodedPolyline.length();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int shift = 0;
            int result = 0;
            int b;
            do {
                b = encodedPolyline.charAt(index++) - 63;
                result |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);
            final int deltaLat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += deltaLat;

            shift = 0;
            result = 0;
            do {
                b = encodedPolyline.charAt(index++) - 63;
                result |= (b & 0x1F) << shift;
                shift += 5;
            } while (b >= 0x20);
            final int deltaLng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += deltaLng;

            polyline.add(new LatLng(lat / 1e5, lng / 1e5));
        }

        return polyline;
    }
    // CHECKSTYLE: MagicNumber ON
}
