package org.app.travelmode.model.google.dto.directions;

import java.util.List;

public class DirectionsRoute {

    private final Bounds bounds;
    private final List<DirectionsLeg> legs;
    private final DirectionsPolyline overview_polyline;
    private final String summary;

    public DirectionsRoute(final Bounds bounds, final List<DirectionsLeg> legs,
                           final DirectionsPolyline overview_polyline, final String summary) {
        this.bounds = bounds;
        this.legs = legs;
        this.overview_polyline = overview_polyline;
        this.summary = summary;
    }

    public Bounds getBounds() {
        return this.bounds;
    }

    public List<DirectionsLeg> getLegs() {
        return List.copyOf(this.legs);
    }

    public DirectionsPolyline getOverview_polyline() {
        return this.overview_polyline;
    }

    public String getSummary() {
        return this.summary;
    }

    @Override
    public String toString() {
        return "{Bounds: " + bounds + "\nOverview Polyline: " + overview_polyline +
                "\nSummary: " + summary + "\nLegs: " + legs + "}";
    }


    /**
     * The north east and south west points that delineate the outer bounds of a map.
     */
    public static class Bounds {
        private final LatLng northeast;
        private final LatLng southwest;

        public Bounds(final LatLng northeast, final LatLng southwest) {
            this.northeast = northeast;
            this.southwest = southwest;
        }

        public LatLng getNortheast() {
            return this.northeast;
        }

        public LatLng getSouthwest() {
            return this.southwest;
        }

        @Override
        public String toString() {
            return "{North-East: " + northeast + "\nSouth-West: " + southwest + "}";
        }
    }
}
