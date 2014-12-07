package gamer.def;

@SuppressWarnings("serial")
public class GameException extends RuntimeException {
  public GameException() {
    super();
  }

  public GameException(String message) {
    super(message);
  }
}
