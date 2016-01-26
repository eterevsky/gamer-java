package gamer.dominion;

import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import gamer.def.Position;
import gamer.def.TerminalPositionException;

public final class DominionState
    implements Position<DominionState, DominionMove> {
  enum Phase {
    START_GAME,
    ACTION,
    ACTION_RESOLUTION,
    BUY,
    END_MOVE  // Take cards
  }

  private Dominion game;
  private int player = -1;
  private Map<DominionCard, Integer> supply;
  private List<Deck> decks = new ArrayList<>();
  private List<List<DominionCard>> discards = new ArrayList<>();
  private List<List<DominionCard>> hands = new ArrayList<>();
  private List<DominionCard> playedActions = new ArrayList<>();
  private boolean terminal = false;
  private Phase phase = Phase.GAME_START;

  DominionState(Dominion game) {
    this.game = game;
    supply = new HashMap<>(game.getSupply());
    for (int i = 0; i < game.getPlayersCount(); i++) {
      Deck deck = new Deck();
      for (int j = 0; j < 7; j++) {
        deck.add(Dominion.COPPER);
      }
      for (int j = 0; j < 3; j++) {
        deck.add(Dominion.ESTATE);
      }
      decks.add(deck);
      discards.add(new ArrayList<>());
    }
  }

  // Position<> implementation.

  public int getPlayer() {
    if (phase == Phase.START_GAME || phase == Phase.ACTION_RESOLUTION) {
      return -1;
    }
    return player;
  }

  public boolean isTerminal() {
    return terminal;
  }

  public int getPayoff(int player) {
    if (!terminal)
      throw new TerminalPositionException();

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

  public List<DominionMove> getMoves() {
    switch (phase) {
      case START_GAME:
        return null;  // Too many possible moves.

      case ACTIONS:

    }

  }

  public DominionMove getRandomMove(Random rng) {
    throw new UnsupportedOperationException();
  }

  public void play(DominionMove move) {
    throw new UnsupportedOperationException();
  }

  public DominionMove parseMove(String moveStr) {
    throw new UnsupportedOperationException();
  }

  public DominionState clone() {
    throw new UnsupportedOperationException();
  }

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
    if (supply.get(Dominion.PROVINCE) == 0) {
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
}
