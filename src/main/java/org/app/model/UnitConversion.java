package org.app.model;

public class UnitConversion {

    /**
     * Converte una temperatura da gradi Celsius a Fahrenheit.
     *
     * @param celsius La temperatura in gradi Celsius.
     * @return La temperatura convertita in gradi Fahrenheit.
     */
    public static double celsiusToFahrenheit(final double celsius) {
        return celsius * (9 / 5) + 32;
    }

    /**
     * Converte una lunghezza da millimetri a pollici.
     *
     * @param millimeters La lunghezza in millimetri.
     * @return La lunghezza convertita in pollici.
     */
    public static double mmToInches(final double millimeters) {
        return millimeters / 25.4;
    }

    /**
     * Converte una velocità da chilometri orari a miglia orarie.
     *
     * @param kmPerHour La velocità in chilometri orari.
     * @return La velocità convertita in miglia orarie.
     */
    public static double kmhToMph(final double kmPerHour) {
        return kmPerHour * 0.621371;
    }

}
