package gamer.dominion.cards;

import gamer.dominion.DominionCard;
import gamer.dominion.DominionState;

public final class Dutchy implements DominionCard {
  private static final Dutchy INSTANCE = new Dutchy();

  private Dutchy() {}

  public static Dutchy getInstance() {
    return INSTANCE;
  }

  @Override
  public String getName() { return "Dutchy"; }

  @Override
  public boolean isOptional() { return false;}

  @Override
  public int cost() { return 5; }

  @Override
  public int winningPoints(DominionState state) { return 3; }

  @Override
  public int startingNumber(int nplayers) {
    return nplayers > 2 ? 12 : 8;
  }
}
