package gamer.def;

public interface Game<P extends Position<P, M>, M extends Move> {
  P newGame();

  default MoveSelector<P, M> getRandomMoveSelector() {
    return new GenericRandomMoveSelector<P, M>();
  }

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
