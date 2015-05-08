package gamer.def;

public interface Game {
  Position<?, ?> newGame();

  default int getPlayersCount() {
    return 2;
  }

  /**
   * True if the game has probabilistic moves.
   */
  default boolean isRandom() {
    return false;
  }
}
