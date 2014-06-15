package gamer;

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

    return 0.0;  // Can't happen.
  }

  // From 0 to 2.
  public int asInt() {
    switch (this) {
      case WIN: return 2;
      case LOSS: return 0;
      case DRAW: return 1;
    }

    return 0;
  }
}
