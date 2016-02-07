package gamer.dominion.cards;

import gamer.dominion.DominionCard;
import gamer.dominion.DominionState;

public final class Estate implements DominionCard {
  private static final Estate INSTANCE = new Estate();

  private Estate() {}

  public static Estate getInstance() {
    return INSTANCE;
  }

  @Override
  public String getName() { return "Estate"; }

  @Override
  public boolean isOptional() { return false;}

  @Override
  public int cost() { return 2; }

  @Override
  public int winningPoints(DominionState state) { return 1; }

  @Override
  public int startingNumber(int nplayers) {
    return nplayers > 2 ? 12 : 8;
  }
}
