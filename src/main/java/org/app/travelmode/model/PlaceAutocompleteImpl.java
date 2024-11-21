package org.app.travelmode.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.List;

public class PlaceAutocompleteImpl implements PlaceAutocomplete {

    private final String googleApiKey;

    public PlaceAutocompleteImpl() {
        final String[] inputLine;
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("src/main/resources/API-Keys.json")))) {
            br.readLine();
            inputLine = br.readLine().split(":");
            this.googleApiKey = inputLine[1];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(googleApiKey);
    }

    public List<String> getSuggestion(final String input) {
        return null;
    }

}
