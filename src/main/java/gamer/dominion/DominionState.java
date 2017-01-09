package gamer.dominion;

import gamer.def.IllegalMoveException;
import gamer.def.Position;
import gamer.def.TerminalPositionException;
import gamer.dominion.cards.Copper;
import gamer.dominion.cards.Estate;
import gamer.dominion.cards.Province;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public final class DominionState
    implements Position<DominionState, DominionMove> {
  private Dominion game;
  private int player = -1;
  private Map<DominionCard, Integer> supply;
  private List<Deck> decks = new ArrayList<>();
  private List<List<DominionCard>> discards = new ArrayList<>();
  private List<List<DominionCard>> hands = new ArrayList<>();
  private List<DominionCard> playedCards = new ArrayList<>();
  private ActionState actionState = null;
  private int actions = 1;
  private int buys = 1;
  private int treasure = 0;
  private boolean terminal = false;
  private Phase phase = Phase.START_GAME;

  DominionState(Dominion game) {
    this.game = game;
    supply = new HashMap<DominionCard, Integer>(game.getSupply());
    for (int i = 0; i < game.getPlayersCount(); i++) {
      Deck deck = new Deck();
      for (int j = 0; j < 7; j++) {
        deck.add(Copper.getInstance());
      }
      for (int j = 0; j < 3; j++) {
        deck.add(Estate.getInstance());
      }
      decks.add(deck);
      discards.add(new ArrayList<>());
    }
  }

  @Override
  public int getPlayer() {
    switch (phase) {
      case START_GAME:
        return -1;
      case ACTION_SPECIFIC:
        return actionState.getPlayer();
      default:
        return player;
    }
  }

  // Position<> implementation.

  @Override
  public boolean isTerminal() {
    return terminal;
  }

  @Override
  public int getPayoff(int player) {
    if (!terminal) { throw new TerminalPositionException(); }

    int winner = -1;
    int maxScore = -100;
    for (int iplayer = 0; iplayer < game.getPlayersCount(); iplayer++) {
      int score = streamFullDeck(iplayer)
          .mapToInt(c -> c.winningPoints(this)).sum();
      if (score > maxScore) {
        winner = iplayer;
        maxScore = score;
      }
    }

    return winner == player ? game.getPlayersCount() - 1 : -1;
  }

  @Override
  public List<DominionMove> getMoves() {
    List<DominionMove> moves = null;

    System.err.format("phase: %s\n", phase);

    switch (phase) {
      case ACTION:
        moves = new ArrayList<>();
        moves.add(DominionMove.BUY_PHASE);

        if (actions > 0) {
          for (DominionCard card : hands.get(player)) {
            if (card.isAction()) {
              moves.add(card.getMove());
            }
          }
        }
        break;

      case ACTION_SPECIFIC:
        moves = actionState.getMoves();
        break;

      case BUY:
        moves = new ArrayList<>();
        moves.add(DominionMove.CLEANUP);
        if (buys > 0) {
          for (Map.Entry<DominionCard, Integer> entry : supply.entrySet()) {
            DominionCard card = entry.getKey();
            if (entry.getValue() > 0 && card.cost() <= treasure) {
              moves.add(card.getBuy());
            }
          }
        }
        break;

      case START_GAME:
      case CLEANUP:
        break;  // return null

      default:
        throw new RuntimeException("Unexpected turn phase.");
    }

    return moves;
  }

  @Override
  public void play(DominionMove move) {
		switch (phase) {
			case ACTION:
				if (move == DominionMove.BUY_PHASE) {
					phase = Phase.BUY;
					return;
				}
				throw new IllegalMoveException(this, move);

			case BUY:
					if (move == DominionMove.CLEANUP) {
					  cleanup();
            return;
					}
					throw new IllegalMoveException(this, move);
		}
    throw new UnsupportedOperationException();
  }

  @Override
  public DominionMove parseMove(String moveStr) {
    throw new UnsupportedOperationException();
  }

  @Override
  public DominionState clone() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    throw new UnsupportedOperationException();
  }

  // Dominion-specific actions.

  private void drawNewHand(int iplayer) {
    Deck deck = decks.get(iplayer);
    List<DominionCard> discard = discards.get(iplayer);
    List<DominionCard> hand = hands.get(iplayer);
    for (DominionCard card : hand) {
      discard.add(card);
    }
    hand.clear();

    Random rng = ThreadLocalRandom.current();
    for (int i = 0; i < 5; i++) {
      hand.add(deck.draw(rng));
    }
  }

  private void checkTerminal() {
    if (supply.get(Province.getInstance()) == 0) {
      terminal = true;
      return;
    }
    int count = Collections.frequency(supply.values(), 0);
    terminal = count >= 3 && (game.getPlayersCount() <= 4 || count >= 4);
  }

  private Stream<DominionCard> streamFullDeck(int iplayer) {
    return Stream.concat(
        Stream.concat(decks.get(iplayer).stream(),
            discards.get(iplayer).stream()),
        hands.get(iplayer).stream());
  }

	private void cleanup() {

	}

  enum Phase {
    START_GAME,         // Dealing cards.
    ACTION,             // Select action to perform.
    ACTION_SPECIFIC,    // Action sub-move.
    BUY,                // Buy cards
    CLEANUP             // Take cards
  }
}
