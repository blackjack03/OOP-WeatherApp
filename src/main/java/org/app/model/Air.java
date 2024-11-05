package org.app.model;

import java.util.*;

public interface Air {

    Optional<String> getTodayAirQuality();

    Optional<Map<Integer, Pair<String, Float>>> getTodayAirQualityByHours();

    Optional<String> getTodayPollen();

}
