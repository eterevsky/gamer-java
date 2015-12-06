package gamer.dominion;

public interface DominionCard {
  String getName();

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
}
