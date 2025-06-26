package org.app.weathermode.model;

import java.util.*;

public interface LocationSelector {
    
    List<Pair<String, Integer>> getPossibleLocations(String txt);

    Optional<Map<String, String>> getByID(int ID);

    Optional<Integer> searchByLookUp(LookUp lookUp);

}
