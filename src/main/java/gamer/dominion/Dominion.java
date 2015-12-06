package gamer.dominion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gamer.def.Game;
import gamer.def.GameException;
import gamer.def.Position;
import gamer.dominion.cards.*;

public final class Dominion implements Game {
  public static class Builder {
    int nplayers;
    private List<DominionCard> optionalCards;

    public Builder(int nplayers) {
      this.nplayers = nplayers;
      this.optionalCards = new ArrayList<>();
    }

    public Dominion build() {
      return new Dominion(nplayers, optionalCards);
    }

    public Builder randomizeCards() {
      return this;
    }

    public Builder addCard(String cardName) {
      return addCard(getCardByName(cardName));
    }

    private Builder addCard(DominionCard card) {
      if (!card.isOptional()) {
        throw new GameException("Can't add a non-optional card.");
      }
      for (DominionCard addedCard : optionalCards) {
        if (addedCard == card) {
          throw new GameException("A card already added.");
        }
      }
      optionalCards.add(card);
      return this;
    }
  }

  private static List<DominionCard> CARDS = populateCards();
  static final DominionCard COPPER = getCardByName("Copper");
  static final DominionCard ESTATE = getCardByName("Estate");
  static final DominionCard PROVINCE = getCardByName("Province");

  private final int nplayers;
  private final Map<DominionCard, Integer> piles;

  private Dominion(int nplayers, List<DominionCard> optionalCards) {
    this.nplayers = nplayers;
    this.piles = new HashMap<>();
    addCard(this.piles, "Copper", nplayers);
    addCard(this.piles, "Silver", nplayers);
    addCard(this.piles, "Gold", nplayers);
    addCard(this.piles, "Estate", nplayers);
    addCard(this.piles, "Dutchy", nplayers);
    addCard(this.piles, "Province", nplayers);

    // TODO: check that there are exactly 10 optional cards.
    for (DominionCard card : optionalCards) {
      addCard(this.piles, card.getName(), nplayers);
    }
  }

  public static DominionCard getCardByName(String name) {
    for (DominionCard card : CARDS) {
      if (card.getName().equals(name)) {
        return card;
      }
    }
    throw new GameException("Unknown card name: " + name);
  }

  @Override
  public DominionState newGame() {
    return new DominionState(this);
  }

  @Override
  public int getPlayersCount() {
    return nplayers;
  }

  @Override
  public boolean isRandom() {
    return true;
  }

  Map<DominionCard, Integer> getSupply() {
    return this.piles;
  }

  private static List<DominionCard> populateCards() {
    List<DominionCard> instances = new ArrayList<>();
    instances.add(new Copper());
    instances.add(new Silver());
    instances.add(new Gold());
    instances.add(new Estate());
    instances.add(new Dutchy());
    instances.add(new Province());

    return instances;
  }

  private static void addCard(
      Map<DominionCard, Integer> piles, String cardName, int nplayers) {
    DominionCard card = getCardByName(cardName);
    if (piles.containsKey(card)) {
      throw new GameException("Duplicate card in a game: " + cardName);
    }
    piles.put(card, card.startingNumber(nplayers));
  }
}
