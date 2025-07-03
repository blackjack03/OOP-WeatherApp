package org.app.travelmode.model.google.dto.placeautocomplete;

/**
 * {@code PlaceAutocompletePrediction} represents a single autocomplete suggestion
 * returned by a place search or geocoding service.
 * <p>
 * Each prediction includes a human-readable description, a structured formatting object,
 * and a unique place identifier.
 * </p>
 *
 */
public class PlaceAutocompletePrediction {
    // CHECKSTYLE: MemberName OFF
    private String description;
    private PlaceAutocompleteStructuredFormat structured_formatting;
    private String place_id;
    // CHECKSTYLE: MemberName OFF

    /**
     * Constructs an empty {@code PlaceAutocompletePrediction}.
     */
    public PlaceAutocompletePrediction() {
    }

    /**
     * Returns the full description of the predicted place.
     *
     * @return A human-readable string describing the place (e.g., "Milan, Italy")
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the structured formatting of the predicted place,
     * including main and secondary text parts.
     *
     * @return A {@link PlaceAutocompleteStructuredFormat} object with detailed formatting
     */
    public PlaceAutocompleteStructuredFormat getStructuredFormatting() {
        return this.structured_formatting;
    }

    /**
     * Returns the unique identifier of the predicted place.
     * This ID can be used to request more detailed information about the place.
     *
     * @return A unique place ID string
     */
    public String getPlaceId() {
        return this.place_id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "PlaceAutocompletePrediction [description=" + description
                + ", structuredFormatting=" + structured_formatting
                + ", placeId=" + place_id + "]\n";
    }

    /**
     * {@code PlaceAutocompleteStructuredFormat} contains structured text formatting
     * for a predicted place, typically used for UI rendering.
     * <p>
     * It separates the suggestion into a main title and secondary text
     * (e.g., main: "Milan", secondary: "Italy").
     * </p>
     */
    public static class PlaceAutocompleteStructuredFormat {
        // CHECKSTYLE: MemberName OFF
        private String main_text;
        private String secondary_text;
        // CHECKSTYLE: MemberName OFF

        /**
         * Constructs an empty {@code PlaceAutocompleteStructuredFormat}.
         */
        public PlaceAutocompleteStructuredFormat() {

        }

        /**
         * Returns the main text of the suggestion (e.g., the city name).
         *
         * @return A string containing the main label
         */
        public String getMainText() {
            return this.main_text;
        }

        /**
         * Returns the secondary text of the suggestion (e.g., the country or region).
         *
         * @return A string containing the supporting context
         */
        public String getSecondaryText() {
            return this.secondary_text;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "PlaceAutocompleteStructuredFormat [mainText=" + main_text + ", secondaryText=" + secondary_text + "]";
        }
    }
}
