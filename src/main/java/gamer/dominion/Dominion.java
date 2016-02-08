package gamer.dominion;

import gamer.def.Game;
import gamer.def.GameException;

import java.util.*;

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
      return addCard(Cards.getCardByName(cardName));
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

  private final int nplayers;
  private final Map<DominionCard, Integer> piles;

  private Dominion(int nplayers, List<DominionCard> optionalCards) {
    this.nplayers = nplayers;
    this.piles = new HashMap<>();
    addCard(this.piles, Cards.COPPER, nplayers);
    addCard(this.piles, Cards.SILVER, nplayers);
    addCard(this.piles, Cards.GOLD, nplayers);
    addCard(this.piles, Cards.ESTATE, nplayers);
    addCard(this.piles, Cards.DUTCHY, nplayers);
    addCard(this.piles, Cards.PROVINCE, nplayers);

    // TODO: check that there are exactly 10 optional cards.
    for (DominionCard card : optionalCards) {
      addCard(this.piles, card, nplayers);
    }
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

  private static void addCard(
      Map<DominionCard, Integer> piles, DominionCard card, int nplayers) {
    if (piles.containsKey(card)) {
      throw new GameException("Duplicate card in a game: " + card.getName());
    }
    piles.put(card, card.startingNumber(nplayers));
  }
}
