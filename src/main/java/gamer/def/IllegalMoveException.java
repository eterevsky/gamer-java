package gamer.def;

public class IllegalMoveException extends GameException {
  private static final long serialVersionUID = 1L;

  public <P extends Position<P, M>, M extends Move> IllegalMoveException(
      P position, M move, String message) {
    super("Illegal move " + position.moveToString(move) +
          " in position " + position.toString() +
          ": " + message);
  }

  public <P extends Position<P, M>, M extends Move> IllegalMoveException(
      P position, M move) {
    super("Illegal move " + position.moveToString(move) +
          " in position " + position.toString());
  }
}
