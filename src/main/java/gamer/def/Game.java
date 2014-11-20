package gamer.def;

public interface Game {
  Position<?, ?> newGame();

  MutablePosition<?, ?> newGameMut();

  int getPlayers();

  boolean hasRandomPlayer();
}
