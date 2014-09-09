package gamer.gomoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gamer.def.GameException;
import gamer.def.GameStatus;

import java.util.Random;
import org.junit.Test;

public class TestGomokuState {

  @Test(timeout=10)
  public void playVertical() {
    GomokuState state = new GomokuState();
    assertTrue(state.status().getPlayer());
    state = state.play(GomokuMove.create('X', 5, 5));
    assertFalse(state.status().getPlayer());
    state = state.play(GomokuMove.create('O', 4, 5));
    assertTrue(state.status().getPlayer());
    state = state.play(GomokuMove.create('X', 5, 6));
    state = state.play(GomokuMove.create('O', 4, 6));
    state = state.play(GomokuMove.create('X', 5, 7));
    state = state.play(GomokuMove.create('O', 4, 7));
    state = state.play(GomokuMove.create('X', 5, 8));
    state = state.play(GomokuMove.create('O', 4, 8));
    state = state.play(GomokuMove.create('X', 5, 9));

    assertEquals(GameStatus.WIN, state.status());
  }

  @Test(timeout=100)
  public void playHorizontal() {
    GomokuState state = new GomokuState();
    state = state.play(GomokuMove.create('X', 2, 3));
    state = state.play(GomokuMove.create('O', 5, 5));
    state = state.play(GomokuMove.create('X', 4, 3));
    state = state.play(GomokuMove.create('O', 6, 6));
    state = state.play(GomokuMove.create('X', 1, 3));
    state = state.play(GomokuMove.create('O', 7, 7));
    state = state.play(GomokuMove.create('X', 0, 3));
    state = state.play(GomokuMove.create('O', 8, 8));
    state = state.play(GomokuMove.create('X', 3, 3));

    assertEquals(GameStatus.WIN, state.status());
  }

  @Test(timeout=10)
  public void playDiagonal1O() {
    GomokuState state = new GomokuState();
    state = state.play(GomokuMove.create('X', 2, 3));
    state = state.play(GomokuMove.create('O', 5, 5));
    state = state.play(GomokuMove.create('X', 4, 3));
    state = state.play(GomokuMove.create('O', 6, 6));
    state = state.play(GomokuMove.create('X', 1, 3));
    state = state.play(GomokuMove.create('O', 7, 7));
    state = state.play(GomokuMove.create('X', 0, 3));
    state = state.play(GomokuMove.create('O', 8, 8));
    state = state.play(GomokuMove.create('X', 10, 10));
    state = state.play(GomokuMove.create('O', 9, 9));

    assertEquals(GameStatus.LOSS, state.status());
  }

  @Test(timeout=10)
  public void playDiagonal2O() {
    GomokuState state = new GomokuState();
    state = state.play(GomokuMove.create('X', 2, 3));
    state = state.play(GomokuMove.create('O', 6, 5));
    state = state.play(GomokuMove.create('X', 4, 3));
    state = state.play(GomokuMove.create('O', 5, 6));
    state = state.play(GomokuMove.create('X', 1, 3));
    state = state.play(GomokuMove.create('O', 4, 7));
    state = state.play(GomokuMove.create('X', 0, 3));
    state = state.play(GomokuMove.create('O', 3, 8));
    state = state.play(GomokuMove.create('X', 10, 10));
    state = state.play(GomokuMove.create('O', 2, 9));

    assertEquals(GameStatus.LOSS, state.status());
  }

  @Test(timeout=10)
  public void playHorizontalOverlap() {
    GomokuState state = new GomokuState();
    state = state.play(GomokuMove.create('X', 2, 3));
    state = state.play(GomokuMove.create('O', 0, 1));
    state = state.play(GomokuMove.create('X', 4, 3));
    state = state.play(GomokuMove.create('O', 1, 1));
    state = state.play(GomokuMove.create('X', 1, 3));
    state = state.play(GomokuMove.create('O', 2, 1));
    state = state.play(GomokuMove.create('X', 0, 3));
    state = state.play(GomokuMove.create('O', 18, 0));
    state = state.play(GomokuMove.create('X', 10, 10));
    state = state.play(GomokuMove.create('O', 17, 0));

    assertFalse(state.isTerminal());
  }

  @Test(expected = GameException.class, timeout=50)
  public void playNoMoveAfterEnd() {
    GomokuState state = new GomokuState();
    state = state.play(GomokuMove.create('X', 2, 3));
    state = state.play(GomokuMove.create('O', 6, 5));
    state = state.play(GomokuMove.create('X', 4, 3));
    state = state.play(GomokuMove.create('O', 5, 6));
    state = state.play(GomokuMove.create('X', 1, 3));
    state = state.play(GomokuMove.create('O', 4, 7));
    state = state.play(GomokuMove.create('X', 0, 3));
    state = state.play(GomokuMove.create('O', 3, 8));
    state = state.play(GomokuMove.create('X', 10, 10));
    state = state.play(GomokuMove.create('O', 2, 9));

    assertEquals(GameStatus.LOSS, state.status());

    state = state.play(GomokuMove.create('X', 1, 1));
  }

  @Test(expected = GameException.class, timeout=10)
  public void playTwoMovesBySamePlayer() {
    GomokuState state = new GomokuState();
    state = state.play(GomokuMove.create('X', 2, 3));
    state = state.play(GomokuMove.create('X', 6, 5));
  }

  @Test(expected = GameException.class, timeout=50)
  public void playTwoMovesBySamePlace() {
    GomokuState state = new GomokuState();
    state = state.play(GomokuMove.create('X', 2, 3));
    state = state.play(GomokuMove.create('O', 2, 3));
  }

  @Test(timeout=100)
  public void playDraw() {
    GomokuState state = new GomokuState();
    for (int i = 0; i < Gomoku.SIZE; i++) {
      int row;
      switch (i % 4) {
        case 1: row = i + 1; break;
        case 2: row = i - 1; break;
        default: row = i;
      }

      for (int j = 0; j < Gomoku.SIZE; j++) {
        if ((i + j) % 2 == 0) {
          state = state.play(GomokuMove.create('X', row, j));
        } else {
          state = state.play(GomokuMove.create('O', row, j));
        }
      }
    }

    assertTrue(state.isTerminal());
    assertEquals(GameStatus.DRAW, state.status());
  }

  @Test(timeout=50)
  public void playRandomMoves() {
    GomokuState state = new GomokuState();
    Random random = new Random(1234567890L);

    assertTrue(state.status().getPlayer());
    int moves = 0;

    while (!state.isTerminal()) {
      state = state.play(state.getRandomMove(random));
      moves++;
    }

    state.status();
    assertTrue(moves >= 9);
    assertTrue(moves <= Gomoku.CELLS);
  }
}
