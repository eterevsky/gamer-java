package gamer.dominion.cards;

import gamer.dominion.DominionCard;
import gamer.dominion.DominionMove;

public final class Silver implements DominionCard {
  private static final Silver INSTANCE = new Silver();
  private static final DominionMove BUY = DominionMove.buyCard(INSTANCE);

  private Silver() {}

  public static Silver getInstance() {
    return INSTANCE;
  }

  @Override
  public String getName() { return "Silver"; }

  @Override
  public boolean isOptional() { return false;}

  @Override
  public int buyingValue() { return 2; }

  @Override
  public int cost() { return 3; }

  @Override
  public int startingNumber(int nplayers) {
    return nplayers > 4 ? 80 : 40;
  }

  @Override
  public DominionMove getBuy() { return BUY; }
}
