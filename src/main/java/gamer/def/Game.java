package gamer.def;

public interface Game {
  Position<?, ?> newGame();

  PositionMut<?, ?> newGameMut();

  int getPlayers();

  boolean hasRandomPlayer();
}
