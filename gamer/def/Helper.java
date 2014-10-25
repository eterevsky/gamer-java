package gamer.def;

public interface Helper<G extends Game> {
  public static class Result {
    public GameStatus status;
    public int moves;

    public Result(GameStatus status, int moves) {
      this.status = status;
      this.moves = moves;
    }

    public double value() {
      switch (status) {
        case WIN: return 1.0 - 0.0001 * moves;
        case LOSS: return 0.0 + 0.0001 * moves;
        case DRAW: return 0.5;
        default: return -1.0;
      }
    }
  }

  // null in case result is unknown.
  Result evaluate(GameState<G> state);
}
