package org.app.travelmode.model.google.dto.directions;

/**
 * Represents an encoded polyline string that defines a route's geometry in Google Maps.
 * This class encapsulates the encoded polyline points that can be used to draw
 * the route path on a map.
 *
 * <p>A polyline is a series of latitude/longitude coordinates encoded into a single string
 * using Google's polyline encoding algorithm. This encoding:
 * <ul>
 *     <li>Reduces data size for transmission</li>
 *     <li>Maintains precision of geographic coordinates</li>
 *     <li>Can be decoded to reconstruct the exact path</li>
 * </ul>
 */
public class DirectionsPolyline {
    private final String points;

    /**
     * Constructs a new DirectionsPolyline with the specified encoded points string.
     *
     * @param points the encoded polyline string representing the route geometry
     */
    public DirectionsPolyline(final String points) {
        this.points = points;
    }

    /**
     * Returns the encoded polyline points string.
     *
     * @return the encoded polyline string representing the route geometry
     */
    public String getPoints() {
        return points;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "DirectionsPolyline [points= " + points + "]";
    }
}
