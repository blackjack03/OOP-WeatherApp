package org.app.model;

import java.util.Optional;

public class IPLookUp implements IPLookUpInterface {

    private final String API_URL = "https://api.codetabs.com/v1/geolocation/json";
    private String ip = "";
    private String countryCode = "";
    private String countryName = "";
    private String regionName = "";
    private String city = "";
    private String zipCode = "";
    private String timeZone = "";
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
        return this.countryCode;
    }

    @Override
    public String getCountry() {
        return this.countryName;
    }

    @Override
    public String getRegion() {
        return this.regionName;
    }

    @Override
    public String getCity() {
        return this.city;
    }

    @Override
    public String getZipCode() {
        return this.zipCode;
    }

    @Override
    public String getTimeZone() {
        return this.timeZone;
    }

    @Override
    public Pair<Double, Double> getCoords() {
        return this.coords;
    }

    private void clear() {
        this.ip = "";
        this.countryCode = "";
        this.countryName = "";
        this.regionName = "";
        this.city = "";
        this.zipCode = "";
        this.timeZone = "";
        this.coords = null;
    }

    private boolean doLookUpReq() {
        try {
            final AdvancedJsonReader ipinfo = new AdvancedJsonReaderImpl(this.API_URL);
            this.ip = ipinfo.getString("ip");
            this.countryCode = ipinfo.getString("country_code");
            this.countryName = ipinfo.getString("country_name");
            this.regionName = ipinfo.getString("region_name");
            this.city = ipinfo.getString("city");
            this.zipCode = ipinfo.getString("zip_code");
            this.timeZone = ipinfo.getString("time_zone");
            this.coords = new Pair<> /* <Double, Double> */
                (ipinfo.getDouble("latitude"),
                ipinfo.getDouble("longitude"));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}