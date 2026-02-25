package com.daniel.greencampus;

public enum Mood {
    VERY_BAD(1, "😞 Very bad"),
    OKAY(2, "😐 Okay"),
    GOOD(3, "🙂 Good"),
    GREAT(4, "😄 Great"),
    AMAZING(5, "🤩 Amazing");

    private final int value;
    private final String label;

    Mood(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return label;
    }

    public static Mood fromValue(int v) {
        for (Mood m : values())
            if (m.value == v) return m;
        return GOOD;
    }
}
