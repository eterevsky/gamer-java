package gamer.def;

public interface Game {
  Position<?, ?> newGame();

  int getPlayersCount();

  /**
   * True if the game has probabilistic moves.
   */
  default boolean isRandom() {
    return false;
  }
}
