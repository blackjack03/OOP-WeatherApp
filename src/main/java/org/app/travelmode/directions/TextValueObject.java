package org.app.travelmode.directions;

public class TextValueObject {
    private final String text;
    private final double value;

    public TextValueObject(final String text, double value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return this.text;
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "TextValueObject [text=" + text + ", value=" + value + "]";
    }
}
