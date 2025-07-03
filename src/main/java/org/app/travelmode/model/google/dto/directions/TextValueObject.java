package org.app.travelmode.model.google.dto.directions;

/**
 * {@code TextValueObject} is a simple data container that holds a textual representation
 * and its corresponding numeric value.
 * <p>
 * This class is commonly used for formatted data such as durations or distances,
 * where both human-readable and machine-usable values are needed.
 * </p>
 */
public class TextValueObject {
    private final String text;
    private final double value;

    /**
     * Constructs a new {@code TextValueObject} with the specified text and numeric value.
     *
     * @param text  A human-readable string (e.g., "5 min", "1.2 km")
     * @param value The corresponding numeric value (e.g., 300 seconds, 1200 meters)
     */
    public TextValueObject(final String text, double value) {
        this.text = text;
        this.value = value;
    }

    /**
     * Returns the text representation of the value.
     *
     * @return A {@code String} describing the value (e.g., "5 min")
     */
    public String getText() {
        return this.text;
    }

    /**
     * Returns the numeric value.
     *
     * @return A {@code double} representing the raw value (e.g., 300.0)
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Returns a string representation of this object, including both the text and value.
     *
     * @return A string in the format {@code TextValueObject [text=..., value=...]}
     */
    @Override
    public String toString() {
        return "TextValueObject [text=" + text + ", value=" + value + "]";
    }
}
