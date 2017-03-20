package gamer.def;

public interface Game<P extends Position<P, M>, M extends Move> {
  P newGame();

  default MoveSelector<P, M> getRandomMoveSelector() {
    return new GenericRandomMoveSelector<P, M>();
  }

  default MoveSelector<P, M> getMoveSelector(String selectorType) {
    if (selectorType == "random") {
      return getRandomMoveSelector();
    } else {
      throw new IllegalArgumentException();
    }
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

  default int getMaxPayoff() {
    return 1;
  }

  default int getMinPayoff() {
    return -1;
  }
}
