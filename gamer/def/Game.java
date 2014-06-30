package gamer.def;

public interface Game<T extends Game<T>> {
  GameState<T> newGame();
}
