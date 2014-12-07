package gamer.def;

@SuppressWarnings("serial")
public class IllegalMoveException extends GameException {
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
