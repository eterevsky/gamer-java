package gamer.dominion;

import gamer.def.Move;

import java.util.HashMap;
import java.util.Map;

public final class DominionMove implements Move {
  public static final DominionMove ACTIONS_TO_BUYS =
      new DominionMove("Start buying phase.");
  private final static Map<DominionCard, DominionMove> cardMoves =
      generateCardMoves();

  private final String name;
  private final DominionCard card;

  private DominionMove(String name) {
    this.card = null;
    this.name = name;
  }

  private DominionMove(DominionCard card) {
    this.card = card;
    this.name = "play " + card.getName();
  }

  public static DominionMove playCard(DominionCard card) {
    return cardMoves.get(card);
  }

  private static Map<DominionCard, DominionMove> generateCardMoves() {
    Map<DominionCard, DominionMove> moves = new HashMap<>();
    for (DominionCard card : Dominion.CARDS) {
      moves.put(card, new DominionMove(card));
    }
    return moves;
  }

  @Override
  public String toString() {
    return name;
  }
}