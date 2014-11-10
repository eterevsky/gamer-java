package gamer.def;

public class IllegalMoveException extends GameException {
  public IllegalMoveException(
      GameState<?> state, Move<?> move, String message) {
    super("Illegal move " + move.toString() +
          " in state " + state.toString() +
          ": " + message);
  }

  public IllegalMoveException(GameState<?> state, Move<?> move) {
    super("Illegal move " + move.toString() +
          " in state " + state.toString());
  }
}
