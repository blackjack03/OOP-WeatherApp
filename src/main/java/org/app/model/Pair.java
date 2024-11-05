package org.app.model;

import java.util.Objects;

/*
 *  A standard generic Pair<X, Y>, with getters, hashCode, equals, and toString well implemented.
 */

public class Pair<X, Y> implements AbstractPair<X, Y> {

	private final X X;
	private final Y Y;

	public Pair(final X x, final Y y) {
		super();
		this.X = x;
		this.Y = y;
	}

	@Override
	public X getX() {
		return X;
	}

	@Override
	public Y getY() {
		return Y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(X, Y);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Pair temp = (Pair) obj;
		return Objects.equals(X, temp.X) && Objects.equals(Y, temp.Y);
	}

	@Override
	public String toString() {
		return "[x=" + X + ", y=" + Y + "]";
	}

}
