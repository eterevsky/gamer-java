package gamer.dominion.cards;

import gamer.dominion.DominionCard;
import gamer.dominion.DominionState;

public final class Silver implements DominionCard {
  public String getName() { return "Silver"; }

  public boolean isOptional() { return false;}

  public int buyingValue() { return 2; }

  public int cost() { return 3; }
}
