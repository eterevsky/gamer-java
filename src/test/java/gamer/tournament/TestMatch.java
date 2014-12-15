package gamer.tournament;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import gamer.gomoku.Gomoku;
import gamer.gomoku.GomokuMove;
import gamer.gomoku.GomokuState;
import gamer.players.RandomPlayer;

public final class TestMatch {

  @Test
  public void testCreateMatch() {
    Match<GomokuState, GomokuMove> match = new Match<>(
        Gomoku.getInstance().newGame(),
        new RandomPlayer<GomokuState, GomokuMove>(),
        new RandomPlayer<GomokuState, GomokuMove>());
    assertNotNull(match.toString());
    match.setPayoff(1);
    assertNotNull(match.toString());
  }
}
