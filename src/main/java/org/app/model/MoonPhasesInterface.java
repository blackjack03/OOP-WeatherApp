package org.app.model;

import java.util.*;

public interface MoonPhasesInterface {

    void setDate(int year, int month, int day);

    Optional<Map<String, String>> getMoonInfo();

    String getDate();

    String getImageURL(String image_name);

}
