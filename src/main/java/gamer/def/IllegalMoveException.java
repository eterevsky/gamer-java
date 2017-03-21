package gamer.def;

public class IllegalMoveException extends GameException {
  private static final long serialVersionUID = 1L;

  public <P extends State<P, M>, M extends Move> IllegalMoveException(
      P position, M move, String message) {
    super("Illegal move " + position.moveToString(move) +
          " in position\n" + position.toString() +
          "\n" + message);
  }

  public <P extends State<P, M>, M extends Move> IllegalMoveException(
      P position, M move) {
    super("Illegal move " + position.moveToString(move) +
          " in position\n" + position.toString());
  }
}
