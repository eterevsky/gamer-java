package gamer.dominion.cards;

import gamer.dominion.DominionCard;

public final class Gold implements DominionCard {
  private static final Gold INSTANCE = new Gold();

  private Gold() {}

  public static Gold getInstance() {
    return INSTANCE;
  }

  @Override
  public String getName() { return "Gold"; }

  @Override
  public boolean isOptional() { return false;}

  @Override
  public int buyingValue() { return 3; }

  @Override
  public int cost() { return 6; }

  @Override
  public int startingNumber(int nplayers) {
    return nplayers > 4 ? 60 : 30;
  }
}
