package gamer.dominion.cards;

import gamer.dominion.DominionCard;
import gamer.dominion.DominionState;

public final class Gold implements DominionCard {
  public String getName() { return "Gold"; }

  public boolean isOptional() { return false;}

  public int buyingValue() { return 3; }

  public int cost() { return 6; }

  public int startingNumber(int nplayers) {
    return nplayers > 4 ? 60 : 30;
  }
}
