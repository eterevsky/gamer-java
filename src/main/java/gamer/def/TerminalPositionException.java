package gamer.def;

public class IllegalMoveException extends IllegalArgumentException {
  public <P extends Position<M>, M extends Move> IllegalMoveException(
      P state, M move, String message) {
    super("Illegal move " + position.moveToString(move) +
          " in position " + position.toString() +
          ": " + message);
  }

  public <P extends Position<M>, M extends Move> IllegalMoveException(
      P state, M move) {
    super("Illegal move " + position.moveToString(move) +
          " in position " + state.toString());
  }
}
