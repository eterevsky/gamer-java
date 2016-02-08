package gamer.dominion;

import gamer.def.GameException;
import gamer.dominion.cards.Copper;
import gamer.dominion.cards.Dutchy;
import gamer.dominion.cards.Estate;
import gamer.dominion.cards.Gold;
import gamer.dominion.cards.Province;
import gamer.dominion.cards.Silver;

import java.util.Arrays;
import java.util.List;

class Cards {
  static final DominionCard COPPER = Copper.getInstance();
  static final DominionCard SILVER = Silver.getInstance();
  static final DominionCard GOLD = Gold.getInstance();
  static final DominionCard ESTATE = Estate.getInstance();
  static final DominionCard DUTCHY = Dutchy.getInstance();
  static final DominionCard PROVINCE = Province.getInstance();

  static final List<DominionCard> CARDS =
      Arrays.asList(COPPER, SILVER, GOLD, ESTATE, DUTCHY, PROVINCE);

  static DominionCard getCardByName(String name) {
    for (DominionCard card : CARDS) {
      if (card.getName().equals(name)) {
        return card;
      }
    }
    throw new GameException("Unknown card name: " + name);
  }
}
