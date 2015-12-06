package gamer.dominion.cards;

import gamer.dominion.DominionCard;
import gamer.dominion.DominionState;

public final class Province implements DominionCard {
  public String getName() { return "Province"; }

  public boolean isOptional() { return false;}

  public int cost() { return 8; }

  public int winningPoints(DominionState state) { return 6; }

  public int startingNumber(int nplayers) {
    switch (nplayers) {
      case 2: return 8;
      case 3: return 12;
      case 4: return 12;
      case 5: return 15;
      case 6: return 18;
    }
    throw new RuntimeException("Unexpected number of players.");
  }
}
