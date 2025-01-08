package org.app.model;

import java.util.Optional;

public class IPLookUp implements IPLookUpInterface {

    private final String API_URL = "https://api.codetabs.com/v1/geolocation/json";
    private String ip = "";
    private String country_code = "";
    private String country_name = "";
    private String region_name = "";
    private String city = "";
    private String zip_code = "";
    private String time_zone = "";
    private Pair<Double, Double> coords = null;

    IPLookUp() {
        /* empty body */
    }

    @Override
    public Optional<Boolean> lookup() {
        this.clear();
        final int MAX_ATTEMPTS = 10;
        for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
            if (this.doLookUpReq()) {
                return Optional.of(true);
            }
        }
        return Optional.empty();
    }

    @Override
    public String getIP() {
        return this.ip;
    }

    @Override
    public String getCountryCode() {
        return this.country_code;
    }

    @Override
    public String getCountry() {
        return this.country_name;
    }

    @Override
    public String getRegion() {
        return this.region_name;
    }

    @Override
    public String getCity() {
        return this.city;
    }

    @Override
    public String getZipCode() {
        return this.zip_code;
    }

    @Override
    public String getTimeZone() {
        return this.time_zone;
    }

    @Override
    public Pair<Double, Double> getCoords() {
        return this.coords;
    }

    private void clear() {
        this.ip = "";
        this.country_code = "";
        this.country_name = "";
        this.region_name = "";
        this.city = "";
        this.zip_code = "";
        this.time_zone = "";
        this.coords = null;
    }

    private boolean doLookUpReq() {
        try {
            final AdvancedJsonReader ipinfo = new AdvancedJsonReader(this.API_URL);
            this.ip = ipinfo.getString("ip");
            this.country_code = ipinfo.getString("country_code");
            this.country_name = ipinfo.getString("country_name");
            this.region_name = ipinfo.getString("region_name");
            this.city = ipinfo.getString("city");
            this.zip_code = ipinfo.getString("zip_code");
            this.time_zone = ipinfo.getString("time_zone");
            this.coords = new Pair<> /* <Double, Double> */
                (ipinfo.getDouble("latitude"),
                ipinfo.getDouble("longitude"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}