package org.app.weathermode.model.pair;

import java.util.Objects;

/**
 * Implementazione concreta di {@link AbstractPair} che rappresenta
 * una coppia di valori eterogenei.
 *
 * @param <X> tipo del primo elemento
 * @param <Y> tipo del secondo elemento
 */
public class Pair<X, Y> implements AbstractPair<X, Y> {

    private final X x;
    private final Y y;

    /**
     * Costruisce una coppia con i due valori specificati.
     *
     * @param x il primo elemento della coppia
     * @param y il secondo elemento della coppia
     */
    public Pair(final X x, final Y y) {
        super();
        this.x = x;
        this.y = y;
    }

    /**
     * Restituisce il primo elemento della coppia.
     *
     * @return il valore del primo elemento (X)
     */
    @Override
    public X getX() {
        return x;
    }

    /**
     * Restituisce il secondo elemento della coppia.
     *
     * @return il valore del secondo elemento (Y)
     */
    @Override
    public Y getY() {
        return y;
    }

    /**
     * Calcola l'hash code della coppia basato sui due elementi.
     *
     * @return hash code combinato di x e y
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Confronta questa coppia con un altro oggetto.
     *
     * @param obj l'oggetto da confrontare con questa coppia
     * @return {@code true} se obj è una coppia con gli stessi valori di x e y
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Pair temp = (Pair) obj;
        return Objects.equals(x, temp.x) && Objects.equals(y, temp.y);
    }

    /**
     * Restituisce una rappresentazione in forma di stringa della coppia,
     * nel formato {@code "[x=<valoreX>, y=<valoreY>]"}.
     *
     * @return la stringa che rappresenta la coppia
     */
    @Override
    public String toString() {
        return "[x=" + x + ", y=" + y + "]";
    }

}
