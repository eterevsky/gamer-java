package gamer.def;

public enum GameStatus {
  FIRST_PLAYER,
  SECOND_PLAYER,
  WIN,
  LOSS,
  DRAW;

  public double value() {
    switch (this) {
      case WIN: return 1.0;
      case LOSS: return 0.0;
      case DRAW: return 0.5;
      default: throw new RuntimeException("can't happen");
    }
  }

  public double value(boolean player) {
    if (player) {
      return value();
    } else {
      return 1.0 - value();
    }
  }

  public boolean isTerminal() {
    return this == WIN || this == LOSS || this == DRAW;
  }

  public boolean getPlayer() {
    switch (this) {
      case FIRST_PLAYER: return true;
      case SECOND_PLAYER: return false;
      default: throw new RuntimeException("can't happen");
    }
  }

  public GameStatus otherPlayer() {
    switch (this) {
      case FIRST_PLAYER: return SECOND_PLAYER;
      case SECOND_PLAYER: return FIRST_PLAYER;
      default: throw new RuntimeException("can't happen");
    }
  }
}
