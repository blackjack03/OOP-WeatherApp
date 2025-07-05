package org.app.travelmode.model.google.dto.directions;

import java.util.List;

/**
 * Represents a single route option returned by the Google Directions API.
 * This class contains detailed information about a specific route, including its bounds,
 * legs (segments), polyline representation, and summary description.
 */
@SuppressWarnings("PMD.FieldNamingConventions")
public class DirectionsRoute {

    // CHECKSTYLE: MemberName OFF
    // Fields used by Gson: names must match exactly the received JSON
    private final Bounds bounds;
    private final List<DirectionsLeg> legs;
    private final DirectionsPolyline overview_polyline;
    private final String summary;
    // CHECKSTYLE: MemberName ON

    /**
     * Constructs a new DirectionsRoute with the specified parameters.
     *
     * @param bounds           The geographical bounds containing the entire route
     * @param legs             The list of route segments
     * @param overviewPolyline The encoded polyline representation of the route
     * @param summary          A textual description of the route
     */
    public DirectionsRoute(final Bounds bounds, final List<DirectionsLeg> legs,
                           final DirectionsPolyline overviewPolyline, final String summary) {
        this.bounds = bounds;
        this.legs = legs;
        this.overview_polyline = overviewPolyline;
        this.summary = summary;
    }

    /**
     * Returns the geographical bounds of the route.
     *
     * @return The bounds object containing northeast and southwest points
     */
    public Bounds getBounds() {
        return this.bounds;
    }

    /**
     * Returns an unmodifiable copy of the route legs.
     *
     * @return An unmodifiable list containing the route segments
     */
    public List<DirectionsLeg> getLegs() {
        return List.copyOf(this.legs);
    }

    /**
     * Returns the encoded polyline representation of the route.
     *
     * @return The polyline object for this route
     */
    public DirectionsPolyline getOverviewPolyline() {
        return this.overview_polyline;
    }

    /**
     * Returns the textual description of the route.
     *
     * @return A string containing the route summary
     */
    public String getSummary() {
        return this.summary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "{Bounds: " + bounds + "\nOverview Polyline: " + overview_polyline
                + "\nSummary: " + summary + "\nLegs: " + legs + "}";
    }


    /**
     * Represents the geographical bounds of a route, defined by northeast and southwest points.
     * These bounds form a rectangle that completely contains the route.
     */
    public static class Bounds {
        private final LatLng northeast;
        private final LatLng southwest;

        /**
         * Constructs a new Bounds object with the specified corner points.
         *
         * @param northeast The northeastern corner of the bounding box
         * @param southwest The southwestern corner of the bounding box
         */
        public Bounds(final LatLng northeast, final LatLng southwest) {
            this.northeast = northeast;
            this.southwest = southwest;
        }

        /**
         * Returns the northeastern corner point.
         *
         * @return The LatLng object representing the northeastern point
         */
        public LatLng getNortheast() {
            return this.northeast;
        }

        /**
         * Returns the southwestern corner point.
         *
         * @return The LatLng object representing the southwestern point
         */
        public LatLng getSouthwest() {
            return this.southwest;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "{North-East: " + northeast + "\nSouth-West: " + southwest + "}";
        }
    }
}
