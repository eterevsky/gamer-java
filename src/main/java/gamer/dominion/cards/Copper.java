package gamer.dominion.cards;

import gamer.dominion.DominionCard;
import gamer.dominion.DominionState;

public final class Copper implements DominionCard {
  public String getName() { return "Copper"; }

  public boolean isOptional() { return false;}

  public int buyingValue() { return 1; }

  public int cost() { return 0; }

  public int startingNumber(int nplayers) {
    return (nplayers > 4 ? 120 : 60) - nplayers * 7;
  }
}
