package gamer.gomoku;

import gamer.util.ConfidenceInterval;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestGomokuMoveSelectors {

  @Test(timeout = 100)
  public void playRandomMoves() {
    Gomoku gomoku = Gomoku.getInstance();
    GomokuState.RandomSelector selector = gomoku.getRandomMoveSelector();
    GomokuState state = gomoku.newGame();

    assertTrue(state.getPlayerBool());
    int moves = 0;

    while (!state.isTerminal()) {
      state.play(selector.select(state));
      moves++;
    }

    assertTrue(moves >= 9);
    assertTrue(moves <= gomoku.getSize() * gomoku.getSize());
  }

  @Test(timeout = 50)
  public void playRandomOnSmallBoard() {
    Gomoku gomoku = Gomoku.getInstance(4);
    GomokuState.RandomSelector selector = gomoku.getRandomMoveSelector();

    for (int igame = 0; igame < 10; igame++) {
      GomokuState state = gomoku.newGame();
      int moves = 0;
      while (!state.isTerminal()) {
        state.play(selector.select(state));
        moves++;
      }

      assertEquals(16, moves);
      assertTrue(state.isTerminal());
      assertEquals(0, state.getPayoff(0));
    }
  }

  // 11x11 win: 0.515376 ± 0.000112, loss: 0.484595 ± 0.000112
  // 19x19 win: 0.50685 ± 0.00009
  @Test
  public void resultStatistics() {
    int win = 0;
    int total = 10000;
    Gomoku gomoku = Gomoku.getInstance(19);

    GomokuState.RandomSelector selector = gomoku.getRandomMoveSelector();

    for (int i = 0; i < total; i++) {
      GomokuState state = gomoku.newGame();
      while (!state.isTerminal()) {
        state.play(selector.select(state));
      }

      int payoff = state.getPayoff(0);

      if (payoff == 1) {
        win++;
      }
    }

    ConfidenceInterval.Interval interval = ConfidenceInterval.binomialWilson(
        win, total - win);

    assertTrue(Math.abs(interval.center - 0.50685) < 2 * interval.err + 0.0001);
  }

  @Test(timeout = 100)
  public void playRandomNeighbors() {
    Gomoku gomoku = Gomoku.getInstance();
    GomokuState.RandomNeighborSelector selector = gomoku.getRandomNeighborSelector();
    GomokuState state = gomoku.newGame();

    assertTrue(state.getPlayerBool());
    int moves = 0;
    int SIZE = 19;

    state.play("J10");

    while (!state.isTerminal()) {
      GomokuMove move = selector.select(state);
      int p = move.point;
      assertTrue(p % SIZE > 0 && state.get(p - 1) > 0 ||
                 p % SIZE < SIZE - 1 && state.get(p + 1) > 0 ||
                 p >= SIZE && (state.get(p - SIZE) > 0 ||
                               p % SIZE > 0 && state.get(p - SIZE - 1) > 0 ||
                               p % SIZE < SIZE - 1 && state.get(p - SIZE + 1) > 0) ||
                 p < SIZE*(SIZE - 1) && (state.get(p + SIZE) > 0 ||
                                         p % SIZE > 0 && state.get(p + SIZE - 1) > 0 ||
                                         p % SIZE < SIZE - 1 && state.get(p + SIZE + 1) > 0));
      state.play(move);
      moves++;
    }

    assertTrue(String.format("Expected at least 9 moves, but got: %d", moves),moves >= 9);
    assertTrue(moves <= gomoku.getSize() * gomoku.getSize());
  }

}
