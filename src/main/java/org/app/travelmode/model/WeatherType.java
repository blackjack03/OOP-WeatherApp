package org.app.travelmode.model;

public enum WeatherType {
    CLEAR(0),
    RAIN(30),
    SNOW(40),
    FOG(20),
    HAIL(50),
    WIND(25),
    THUNDERSTORM(60);

    private final double baseImpact;

    WeatherType(double baseImpact) {
        this.baseImpact = baseImpact;
    }

    public double getBaseImpact() {
        return baseImpact;
    }
}

