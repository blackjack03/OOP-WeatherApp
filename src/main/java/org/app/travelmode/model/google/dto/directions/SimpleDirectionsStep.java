package org.app.travelmode.model.google.dto.directions;

/**
 * Represents a basic step in a route's directions, containing essential information about
 * a single segment of the journey.
 *
 * <p>A simple directions step includes:
 * <ul>
 *     <li>Duration of the step</li>
 *     <li>Distance covered</li>
 *     <li>Start location coordinates</li>
 *     <li>End location coordinates</li>
 * </ul>
 */
public class SimpleDirectionsStep {

    private static final int SECONDS_PER_MINUTE = 60;

    // CHECKSTYLE: MemberName OFF
    private final TextValueObject duration;
    private final LatLng end_location;
    private final LatLng start_location;
    private final TextValueObject distance;
    // CHECKSTYLE: MemberName ON

    /**
     * Constructs a new {@code SimpleDirectionsStep} with the specified parameters.
     *
     * @param duration      The time required to complete this step, as a {@link TextValueObject}
     * @param endLocation   The geographical coordinates where this step ends
     * @param startLocation The geographical coordinates where this step begins
     * @param distance      The distance covered in this step, as a {@link TextValueObject}
     */
    public SimpleDirectionsStep(final TextValueObject duration, final LatLng endLocation,
                                final LatLng startLocation, final TextValueObject distance) {
        this.duration = duration;
        this.end_location = endLocation;
        this.start_location = startLocation;
        this.distance = distance;
    }

    /**
     * Constructs a new {@code SimpleDirectionsStep} using raw numeric values for duration and distance.
     * <p>
     * Duration is converted to minutes, and distance is converted to meters,
     * both wrapped into {@link TextValueObject} instances.
     * </p>
     *
     * @param duration      The duration in seconds
     * @param endLocation   The geographical coordinates where this step ends
     * @param startLocation The geographical coordinates where this step begins
     * @param distance      The distance in meters
     */
    public SimpleDirectionsStep(final double duration, final LatLng endLocation,
                                final LatLng startLocation, final double distance) {
        this.duration = new TextValueObject((int) Math.ceil(duration) / SECONDS_PER_MINUTE + " min", duration);
        this.end_location = endLocation;
        this.start_location = startLocation;
        this.distance = new TextValueObject((int) Math.ceil(distance) + " m", distance);
    }

    /**
     * Returns the duration of this step.
     *
     * @return A {@link TextValueObject} representing the duration
     */
    public TextValueObject getDuration() {
        return this.duration;
    }

    /**
     * Returns the end location of this step.
     *
     * @return A {@link LatLng} representing the destination coordinates
     */
    public LatLng getEndLocation() {
        return this.end_location;
    }

    /**
     * Returns the start location of this step.
     *
     * @return A {@link LatLng} representing the starting coordinates
     */
    public LatLng getStartLocation() {
        return this.start_location;
    }

    /**
     * Returns the distance covered in this step.
     *
     * @return A {@link TextValueObject} representing the distance
     */
    public TextValueObject getDistance() {
        return this.distance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "DirectionsStep{" + "[duration= " + duration + "], [distance= " + distance + "],"
                + " [start_location= " + start_location + "], [end_location= " + end_location + "]}";
    }
}
