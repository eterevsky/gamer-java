package gamer.dominion.cards;

import gamer.dominion.DominionCard;
import gamer.dominion.DominionMove;

public final class Copper implements DominionCard {
  private static final Copper INSTANCE = new Copper();
  private static final DominionMove BUY = DominionMove.buyCard(INSTANCE);

  private Copper() {}

  public static Copper getInstance() {
    return INSTANCE;
  }

  @Override
  public String getName() { return "Copper"; }

  @Override
  public boolean isOptional() { return false; }

  @Override
  public int buyingValue() { return 1; }

  @Override
  public int cost() { return 0; }

  @Override
  public int startingNumber(int nplayers) {
    return (nplayers > 4 ? 120 : 60) - nplayers * 7;
  }

  @Override
  public DominionMove getBuy() { return BUY; }
}
