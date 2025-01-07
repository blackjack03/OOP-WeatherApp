package org.app.model;

import java.util.Optional;

public interface IPLookUpInterface {

    Optional<Boolean> lookup();

    String getIP();

    String getCountryCode();

    String getCountry();

    String getRegion();

    String getCity();

    String getZipCode();

    String getTimeZone();

    //   lat   , lon
    Pair<Double, Double> getCoords();

}
