package gamer.dominion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gamer.def.Game;
import gamer.def.GameException;
import gamer.def.Position;

public final class Dominion implements Game {
  public static class Builder {
    private int nplayers;
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

  private final int nplayers;
  private final List<DominionCard> cards;

  private Dominion(int nplayers, List<DominionCard> optionalCards) {
    this.nplayers = nplayers;
    this.cards = new ArrayList<>();
    this.cards.add(getCardByName("Copper"));
    this.cards.add(getCardByName("Silver"));
    this.cards.add(getCardByName("Gold"));
    this.cards.add(getCardByName("Estate"));
    this.cards.add(getCardByName("Dutchy"));
    this.cards.add(getCardByName("Province"));
    // TODO: check that there are exactly 10 optional cards.
    for (DominionCard card : optionalCards) {
      this.cards.add(card);
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

  private static final List<DominionCard> CARDS = populateCards();

  private static class Copper implements DominionCard {
    public String getName() { return "Copper"; }
    public boolean isOptional() { return false;}
    public int buyingValue() { return 1; }
    public int cost() { return 0; }
  }

  private static class Silver implements DominionCard {
    public String getName() { return "Silver"; }
    public boolean isOptional() { return false;}
    public int buyingValue() { return 2; }
    public int cost() { return 3; }
  }

  private static class Gold implements DominionCard {
    public String getName() { return "Gold"; }
    public boolean isOptional() { return false;}
    public int buyingValue() { return 3; }
    public int cost() { return 6; }
  }

  private static class Estate implements DominionCard {
    public String getName() { return "Estate"; }
    public boolean isOptional() { return false;}
    public int cost() { return 2; }
    public int winningPoints(DominionState state) { return 1; }
  }

  private static class Dutchy implements DominionCard {
    public String getName() { return "Dutchy"; }
    public boolean isOptional() { return false;}
    public int cost() { return 5; }
    public int winningPoints(DominionState state) { return 3; }
  }

  private static class Province implements DominionCard {
    public String getName() { return "Province"; }
    public boolean isOptional() { return false;}
    public int cost() { return 8; }
    public int winningPoints(DominionState state) { return 6; }
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
}
