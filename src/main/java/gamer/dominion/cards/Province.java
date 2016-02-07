package gamer.dominion.cards;

import gamer.dominion.DominionCard;
import gamer.dominion.DominionState;

public final class Province implements DominionCard {
  private static final Province INSTANCE = new Province();

  private Province() {}

  public static Province getInstance() {
    return INSTANCE;
  }

  @Override
  public String getName() { return "Province"; }

  @Override
  public boolean isOptional() { return false;}

  @Override
  public int cost() { return 8; }

  @Override
  public int winningPoints(DominionState state) { return 6; }

  @Override
  public int startingNumber(int nplayers) {
    switch (nplayers) {
      case 2:
        return 8;
      case 3:
        return 12;
      case 4:
        return 12;
      case 5:
        return 15;
      case 6:
        return 18;
    }
    throw new RuntimeException("Unexpected number of players.");
  }
}
