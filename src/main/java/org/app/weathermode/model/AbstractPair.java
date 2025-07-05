package org.app.weathermode.model;

/**
 * Interfaccia generica che rappresenta una coppia di valori.
 *
 * @param <X> tipo del primo elemento della coppia
 * @param <Y> tipo del secondo elemento della coppia
 */
public interface AbstractPair<X, Y> {

    /**
     * Restituisce il primo elemento (X) della coppia.
     *
     * @return il valore del primo elemento ({@code X})
     */
    X getX();

    /**
     * Restituisce il secondo elemento della coppia.
     *
     * @return il valore del secondo elemento ({@code Y})
     */
    Y getY();

    /**
     * Calcola l'hash code della coppia, basato sui due elementi.
     *
     * @return hash code combinato dei due elementi
     */
    @Override
    int hashCode();

    /**
     * Verifica se un altro oggetto è uguale a questa coppia.
     *
     * @param obj l'oggetto da confrontare
     * @return {@code true} se l'altro oggetto è anch'esso una coppia
     *         e i due elementi sono uguali a quelli di questa coppia
     */
    @Override
    boolean equals(Object obj);

    /**
     * Restituisce una rappresentazione stringa della coppia.
     *
     * @return stringa nel formato "&lt;X, Y&gt;"
    */
    @Override
    String toString();

}
