package gamer.dominion.cards;

import gamer.dominion.DominionCard;
import gamer.dominion.DominionState;

public final class Estate implements DominionCard {
  public String getName() { return "Estate"; }

  public boolean isOptional() { return false;}

  public int cost() { return 2; }

  public int winningPoints(DominionState state) { return 1; }

  public int startingNumber(int nplayers) {
    return nplayers > 2 ? 12 : 8;
  }
}
