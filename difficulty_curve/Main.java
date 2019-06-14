class Main {
  public static double a = 0;
  public static double b = 0;
  public static void main(String[] args) {
    Difficulty difficulty = Difficulty.NORMAL;
    initCurveAlgo(difficulty, 200, 20);
    int grace = getGracePeriod(50);
  }

  public static void initCurveAlgo(Difficulty diff, int maxBPM, int minBPM) {
    b = Math.pow(1.0 / (diff.getMaxGrace() / diff.getMinGrace()), 1.0/(maxBPM - minBPM));
    a = diff.getMaxGrace() / Math.pow(b, minBPM);
  }

  public static int getGracePeriod(int bpm) {
    if (a == 0 || b == 0) {
      return 0;
    }
    return (int) (a * Math.pow(b, bpm));
  }
}
