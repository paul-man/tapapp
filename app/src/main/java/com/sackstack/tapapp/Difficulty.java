package com.sackstack.tapapp;

public enum Difficulty {
    EASY    (120.0, 500.0, "easy"),
    NORMAL  (80.0, 400.0, "normal"),
    HARD    (45.0, 250.0, "hard");

    private final double minGrace;
    private final double maxGrace;
    private final String label;

    private Difficulty(final double minGrace, final double maxGrace, String label) {
        this.minGrace = minGrace;
        this.maxGrace = maxGrace;
        this.label = label;
    }

    public double getMinGrace() { return minGrace; }
    public double getMaxGrace() { return maxGrace; }
    public String getLabel() { return label; }
}