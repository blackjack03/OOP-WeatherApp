package org.app.travelmode.directions;

/**
 * Each element in the steps of a {@link DirectionsLeg} defines a single step of the calculated directions.
 * A step is the most atomic unit of a direction's route, containing a single step describing a specific, single instruction on the journey.
 */
public class DirectionsStep {

    private TextValueObject duration;
    private LatLng end_location;
    private DirectionsPolyline polyline;
    private LatLng start_location;
    private String travel_mode;
    private TextValueObject distance;

    public DirectionsStep(final TextValueObject duration, final LatLng end_location, final LatLng start_location, final TextValueObject distance) {
        this.duration = duration;
        this.end_location = end_location;
        this.start_location = start_location;
        this.distance = distance;
    }

    public DirectionsStep(final TextValueObject duration, final LatLng end_location, final LatLng start_location,
                          final TextValueObject distance, final String travel_mode, final DirectionsPolyline polyline) {
        this(duration, end_location, start_location, distance);
        this.travel_mode = travel_mode;
        this.polyline = polyline;
    }

    public TextValueObject getDuration() {
        return this.duration;
    }

    public LatLng getEnd_location() {
        return this.end_location;
    }

    public DirectionsPolyline getPolyline() {
        return this.polyline;
    }

    public LatLng getStart_location() {
        return this.start_location;
    }

    public String getTravel_mode() {
        return this.travel_mode;
    }

    public TextValueObject getDistance() {
        return this.distance;
    }

    @Override
    public String toString() {
        return "DirectionsStep{" + "duration= " + duration +
                ",\ndistance= " + distance +
                ",\nstart_location= " + start_location +
                ",\nend_location= " + end_location +
                ",\npolyline= " + polyline +
                ",\ntravel_mode= " + travel_mode;
    }
}
