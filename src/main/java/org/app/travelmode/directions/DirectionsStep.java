package org.app.travelmode.directions;

/**
 * Each element in the steps of a {@link DirectionsLeg} defines a single step of the calculated directions.
 * A step is the most atomic unit of a direction's route, containing a single step describing a specific, single instruction on the journey.
 */
public class DirectionsStep extends SimpleDirectionsStep {

    private final DirectionsPolyline polyline;
    private final String travel_mode;

    public DirectionsStep(final TextValueObject duration, final LatLng end_location, final LatLng start_location,
                          final TextValueObject distance, final String travel_mode, final DirectionsPolyline polyline) {
        super(duration, end_location, start_location, distance);
        this.travel_mode = travel_mode;
        this.polyline = polyline;
    }

    public DirectionsPolyline getPolyline() {
        return this.polyline;
    }

    public String getTravel_mode() {
        return this.travel_mode;
    }

    public String toStringBasic() {
        return super.toString();
    }

    @Override
    public String toString() {
        return "DirectionsStep{" + "[duration= " + super.getDuration() + "], [distance= " + super.getDistance()
                + "], start_location= " + super.getStart_location() + "], [end_location= " + super.getEnd_location()
                + "], [polyline= " + polyline + "], [travel_mode= " + travel_mode + "]}";
    }
}
