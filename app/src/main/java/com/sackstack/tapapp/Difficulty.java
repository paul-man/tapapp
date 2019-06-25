package com.sackstack.tapapp;

public enum Difficulty {
    EASY    (120.0, 300.0, "easy"),
    NORMAL  (80.0, 250.0, "normal"),
    HARD    (45.0, 150.0, "hard");

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