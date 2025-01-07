package org.app.travelmode.placeautocomplete;

public class PlaceAutocompletePrediction {
    private String description;
    private PlaceAutocompleteStructuredFormat structured_formatting;
    private String place_id;

    public PlaceAutocompletePrediction() {
    }

    public String getDescription() {
        return this.description;
    }

    public PlaceAutocompleteStructuredFormat getStructuredFormatting() {
        return this.structured_formatting;
    }

    public String getPlaceId() {
        return this.place_id;
    }

    @Override
    public String toString() {
        return "PlaceAutocompletePrediction [description=" + description + ", structuredFormatting=" + structured_formatting + ", placeId=" + place_id + "]\n";
    }

    public static class PlaceAutocompleteStructuredFormat {
        private String main_text;
        private String secondary_text;

        public PlaceAutocompleteStructuredFormat() {

        }

        public String getMainText() {
            return this.main_text;
        }

        public String getSecondaryText() {
            return this.secondary_text;
        }

        @Override
        public String toString() {
            return "PlaceAutocompleteStructuredFormat [mainText=" + main_text + ", secondaryText=" + secondary_text + "]";
        }
    }
}
