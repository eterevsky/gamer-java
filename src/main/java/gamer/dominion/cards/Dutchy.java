package gamer.dominion.cards;

import gamer.dominion.DominionCard;
import gamer.dominion.DominionState;

public final class Dutchy implements DominionCard {
  public String getName() { return "Dutchy"; }

  public boolean isOptional() { return false;}

  public int cost() { return 5; }

  public int winningPoints(DominionState state) { return 3; }

  public int startingNumber(int nplayers) {
    return nplayers > 2 ? 12 : 8;
  }
}
