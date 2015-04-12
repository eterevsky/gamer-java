package gamer.def;

public interface Game {
  Position<?, ?> newGame();

  PositionMut<?, ?> newGameMut();

  int getPlayersCount();

  boolean hasRandomPlayer();
}
