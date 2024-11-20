package org.app.travelmode.model;

import java.util.List;

public interface PlaceAutocomplete {

    /**
     * Provides a list of cities or addresses based on the input text
     *
     * @param input String representing the characters written by the user
     * @return a list of cities or addresses based on the input
     */
    List<String> getSuggestion(final String input);
}
