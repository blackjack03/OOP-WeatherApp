package org.app.weathermode.model;

import java.util.*;

public interface MoonPhases {

    void setDate(int year, int month, int day);

    Optional<Map<String, String>> getMoonInfo();

    String getDate();

    String getImageURL(String image_name);

}
