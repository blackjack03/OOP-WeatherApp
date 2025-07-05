package org.app.travelmode.model.google.dto.directions;

/**
 * {@code DirectionsStep} extends {@link SimpleDirectionsStep} by adding additional
 * route details such as a polyline for visual representation and the travel mode used.
 * <p>
 * This class is typically used to represent a single step in a route with both
 * geometric and descriptive travel information.
 * </p>
 *
 * <p>Additional attributes include:</p>
 * <ul>
 *   <li>{@link DirectionsPolyline} polyline – the encoded path for the step</li>
 *   <li>{@code String} travel_mode – the mode of transportation (e.g., "DRIVING", "WALKING")</li>
 * </ul>
 */
@SuppressWarnings("PMD.FieldNamingConventions")
public class DirectionsStep extends SimpleDirectionsStep {

    // CHECKSTYLE: MemberName OFF
    // Fields used by Gson: names must match exactly the received JSON
    private final DirectionsPolyline polyline;
    private final String travel_mode;
    // CHECKSTYLE: MemberName ON

    /**
     * Constructs a new {@code DirectionsStep} with the given route step data.
     *
     * @param duration      The duration of the step as a {@link TextValueObject}
     * @param endLocation   The coordinates where the step ends
     * @param startLocation The coordinates where the step begins
     * @param distance      The distance of the step as a {@link TextValueObject}
     * @param travelMode    The mode of travel used for this step (e.g., "WALKING", "DRIVING")
     * @param polyline      The {@link DirectionsPolyline} representing the path of this step
     */
    public DirectionsStep(final TextValueObject duration, final LatLng endLocation, final LatLng startLocation,
                          final TextValueObject distance, final String travelMode, final DirectionsPolyline polyline) {
        super(duration, endLocation, startLocation, distance);
        this.travel_mode = travelMode;
        this.polyline = polyline;
    }

    /**
     * Returns the {@link DirectionsPolyline} associated with this step.
     *
     * @return The polyline representing the path of this step
     */
    public DirectionsPolyline getPolyline() {
        return this.polyline;
    }

    /**
     * Returns the travel mode used in this step.
     *
     * @return A string representing the mode of transportation
     */
    public String getTravelMode() {
        return this.travel_mode;
    }

    /**
     * Returns a basic string representation of this step, as inherited from the superclass.
     *
     * @return A string describing duration, distance, and coordinates
     */
    public String toStringBasic() {
        return super.toString();
    }

    /**
     * Returns a full string representation of this step, including all extended properties.
     *
     * @return A string including duration, distance, coordinates, polyline, and travel mode
     */
    @Override
    public String toString() {
        return "DirectionsStep{" + "[duration= " + super.getDuration() + "], [distance= " + super.getDistance()
                + "], start_location= " + super.getStartLocation() + "], [end_location= " + super.getEndLocation()
                + "], [polyline= " + polyline + "], [travel_mode= " + travel_mode + "]}\n";
    }
}
