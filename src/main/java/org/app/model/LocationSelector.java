package org.app.model;

import java.util.*;
import java.io.*;

public class LocationSelector implements LocationSelectorInterface {

    private final String CSV_PATH =
            "src%Smain%Sjava%Sorg%Sfiles%Sworldcities.csv"
            .replace("%S", File.separator);

    final List<Map<String, String>> CSV;
    final Map<Integer, Map<String, String>> CITIES_MAP = new HashMap<>();

    public LocationSelector() {
        try {
            final CSVParser parser = new CSVParser(CSV_PATH);
            this.CSV = parser.readCSVToMap();
            for (int i = 0; i < CSV.size(); i++) {
                final int idx = Integer.parseInt(CSV.get(i).get("id"));
                this.CITIES_MAP.put(idx, CSV.get(i));
            }
            parser.close();
        } catch (final Exception err) {
            throw new Error(err);
        }
        System.out.println("Cities ready!");
    }

    @Override
    public List<Pair<String, Integer>> getPossibleLocations(final String txt) {
        final List<Pair<String, Integer>> possibleLocations = new ArrayList<>();
        final String query = txt.toLowerCase();

        for (int i = 0; i < this.CSV.size(); i++) {
            final Map<String, String> entry = CSV.get(i);
            final String cityName = entry.get("city").toLowerCase();
            final String cityNameAscii = entry.get("city_ascii").toLowerCase();
            if (cityName.contains(query) || cityNameAscii.contains(query)) {
                final var complete_name = new StringBuilder();
                complete_name.append(entry.get("city"));
                complete_name.append(", ");
                complete_name.append(entry.get("admin_name"));
                complete_name.append(", ");
                complete_name.append(entry.get("country"));
                final var city = new Pair<>(complete_name.toString(),
                        Integer.parseInt(entry.get("id")));
                possibleLocations.add(city);
            }
        }

        return possibleLocations;
    }

    @Override
    public Optional<Map<String, String>> getByID(final int ID) {
        return this.CITIES_MAP.containsKey(ID) ?
                Optional.of(CITIES_MAP.get(ID)) :
                Optional.empty();
    }

}
