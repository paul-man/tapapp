public enum Difficulty {
    EASY    (120.0, 500.0),
    NORMAL  (80.0, 400.0),
    HARD    (45.0, 250.0);

    private final double minGrace;
    private final double maxGrace;

    private Difficulty(final double minGrace, final double maxGrace) {
        this.minGrace = minGrace;
        this.maxGrace = maxGrace;
    }

    public double getMinGrace() { return minGrace; }
    public double getMaxGrace() { return maxGrace; }
}
