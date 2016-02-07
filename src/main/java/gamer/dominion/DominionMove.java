package gamer.dominion;

import gamer.def.Move;

public final class DominionMove implements Move {
  public static final DominionMove BUY_PHASE = new DominionMove("Start buy phase.");
  public static final DominionMove CLEANUP = new DominionMove("Cleanup and end the turn.");

  private final String name;
  private final DominionCard card;
  private final boolean play;
  private final boolean buy;

  private DominionMove(String name) {
    this.card = null;
    this.name = name;
    this.play = false;
    this.buy = false;
  }

  private DominionMove(DominionCard card, boolean buy) {
    this.card = card;
    this.name = (buy ? "buy " : "play ") + card.getName();
    this.buy = buy;
    this.play = !buy;
  }

  public static DominionMove playCard(DominionCard card) {
    return new DominionMove(card, false);
  }

  public static DominionMove buyCard(DominionCard card) {
    return new DominionMove(card, true);
  }

  DominionCard getCard() {
    return card;
  }

  @Override
  public String toString() {
    return name;
  }
}