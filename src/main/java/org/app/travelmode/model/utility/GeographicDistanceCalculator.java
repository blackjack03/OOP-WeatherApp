package org.app.travelmode.model.utility;

import org.app.travelmode.model.google.dto.directions.LatLng;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * Utility class used to calculate the distance between two points on the Earth's surface.
 */
public final class GeographicDistanceCalculator {
    private static final double EARTH_RADIUS = 6372.795477598;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private GeographicDistanceCalculator() {
    }

    /**
     * Calculate the distance between two points on Earth.
     *
     * @param p1 first point
     * @param p2 second point
     * @return a {@link BigDecimal} representing the distance of two points on earth in meters
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public static BigDecimal computeDistance(final LatLng p1, final LatLng p2) {
        if (p1.equals(p2)) {
            return BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
        }

        final double radiantLat1 = Math.toRadians(p1.getLat());
        final double radiantLng1 = Math.toRadians(p1.getLng());
        final double radiantLat2 = Math.toRadians(p2.getLat());
        final double radiantLng2 = Math.toRadians(p2.getLng());
        //R * arccos(sin(latA) * sin(latB) + cos(latA) * cos(latB) * cos(lonA-lonB))
        return BigDecimal.valueOf((EARTH_RADIUS
                        * Math.acos((Math.sin(radiantLat1) * Math.sin(radiantLat2)
                        + Math.cos(radiantLat1) * Math.cos(radiantLat2)
                        * Math.cos(radiantLng1 - radiantLng2)))) * 1000)
                .setScale(1, RoundingMode.HALF_UP);
    }

}
