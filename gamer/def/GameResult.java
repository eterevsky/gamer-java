package gamer.def;

public enum GameResult {
  WIN,
  LOSS,
  DRAW;

  // From 0 to 1.
  public double asDouble() {
    switch (this) {
      case WIN: return 1.0;
      case LOSS: return 0.0;
      case DRAW: return 0.5;
    }

    throw new RuntimeException("can't happen");
  }

  // From 0 to 2.
  public int asInt() {
    switch (this) {
      case WIN: return 2;
      case LOSS: return 0;
      case DRAW: return 1;
    }

    throw new RuntimeException("can't happen");
  }
}
