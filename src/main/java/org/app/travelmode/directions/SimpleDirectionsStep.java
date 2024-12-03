package org.app.travelmode.directions;

/**
 * It represents a simple step between two points on a journey
 */
public class SimpleDirectionsStep {

    private final TextValueObject duration;
    private final LatLng end_location;
    private final LatLng start_location;
    private final TextValueObject distance;

    public SimpleDirectionsStep(final TextValueObject duration, final LatLng end_location, final LatLng start_location, final TextValueObject distance) {
        this.duration = duration;
        this.end_location = end_location;
        this.start_location = start_location;
        this.distance = distance;
    }

    public TextValueObject getDuration() {
        return this.duration;
    }

    public LatLng getEnd_location() {
        return this.end_location;
    }

    public LatLng getStart_location() {
        return this.start_location;
    }

    public TextValueObject getDistance() {
        return this.distance;
    }

    @Override
    public String toString() {
        return "DirectionsStep{" + "[duration= " + duration + "], [distance= " + distance + "], [start_location= " + start_location + "], [end_location= " + end_location + "]}";
    }
}
