package org.app.travelmode.model;

import org.app.travelmode.directions.LatLng;


/**
 * Utility class used to calculate the distance between two points on the Earth's surface
 */
public class GeographicDistanceCalculator {
    private static final double EARTH_RADIUS = 6372.795477598;

    public GeographicDistanceCalculator() {
    }

    /**
     * Calculate the distance between two points on Earth
     * @param p1 first point
     * @param p2 second point
     * @return the distance of two points on earth in kilometers
     */
    public static double computeDistance(final LatLng p1, final LatLng p2) {
        double radiantLat1 = Math.toRadians(p1.getLat());
        double radiantLng1 = Math.toRadians(p1.getLng());
        double radiantLat2 = Math.toRadians(p2.getLat());
        double radiantLng2 = Math.toRadians(p2.getLng());
        //R * arccos(sin(latA) * sin(latB) + cos(latA) * cos(latB) * cos(lonA-lonB))
        return EARTH_RADIUS * Math.acos((Math.sin(radiantLat1) * Math.sin(radiantLat2) + Math.cos(radiantLat1) * Math.cos(radiantLat2) * Math.cos(radiantLng1 - radiantLng2)));
    }

}
