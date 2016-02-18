package gamer.dominion;

import gamer.def.GameException;

public interface DominionCard {
  String getName();

  DominionMove getBuy();

  default DominionMove getMove() {
    throw new GameException("Not an action.");
  }

  default boolean isOptional() {
    return true;
  }

  default int buyingValue() {
    return 0;
  }

  int cost();

  default int winningPoints(DominionState state) {
    return 0;
  }

  default int startingNumber(int nplayers) {
    return 10;
  }

  default boolean isAction() { return false; }

  default ActionState play() {
    throw new GameException("Not an action card.");
  }
}
