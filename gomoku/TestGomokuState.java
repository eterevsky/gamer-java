package gomoku;

import gamer.GameException;
import gamer.GameResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestGomokuState {

  @Test
  public void playVertical() {
    GomokuState state = new GomokuState();
    state.play(GomokuMove.create('X', 5, 5));
    state.play(GomokuMove.create('O', 4, 5));
    state.play(GomokuMove.create('X', 5, 6));
    state.play(GomokuMove.create('O', 4, 6));
    state.play(GomokuMove.create('X', 5, 7));
    state.play(GomokuMove.create('O', 4, 7));
    state.play(GomokuMove.create('X', 5, 8));
    state.play(GomokuMove.create('O', 4, 8));
    state.play(GomokuMove.create('X', 5, 9));

    assertTrue(state.isTerminal());
    assertEquals(GameResult.WIN, state.getResult());
  }

  @Test
  public void playHorizontal() {
    GomokuState state = new GomokuState();
    state.play(GomokuMove.create('X', 2, 3));
    state.play(GomokuMove.create('O', 5, 5));
    state.play(GomokuMove.create('X', 4, 3));
    state.play(GomokuMove.create('O', 6, 6));
    state.play(GomokuMove.create('X', 1, 3));
    state.play(GomokuMove.create('O', 7, 7));
    state.play(GomokuMove.create('X', 0, 3));
    state.play(GomokuMove.create('O', 8, 8));
    state.play(GomokuMove.create('X', 3, 3));

    assertTrue(state.isTerminal());
    assertEquals(GameResult.WIN, state.getResult());
  }

  @Test
  public void playDiagonal1O() {
    GomokuState state = new GomokuState();
    state.play(GomokuMove.create('X', 2, 3));
    state.play(GomokuMove.create('O', 5, 5));
    state.play(GomokuMove.create('X', 4, 3));
    state.play(GomokuMove.create('O', 6, 6));
    state.play(GomokuMove.create('X', 1, 3));
    state.play(GomokuMove.create('O', 7, 7));
    state.play(GomokuMove.create('X', 0, 3));
    state.play(GomokuMove.create('O', 8, 8));
    state.play(GomokuMove.create('X', 10, 10));
    state.play(GomokuMove.create('O', 9, 9));

    assertTrue(state.isTerminal());
    assertEquals(GameResult.LOSS, state.getResult());
  }

  @Test
  public void playDiagonal2O() {
    GomokuState state = new GomokuState();
    state.play(GomokuMove.create('X', 2, 3));
    state.play(GomokuMove.create('O', 6, 5));
    state.play(GomokuMove.create('X', 4, 3));
    state.play(GomokuMove.create('O', 5, 6));
    state.play(GomokuMove.create('X', 1, 3));
    state.play(GomokuMove.create('O', 4, 7));
    state.play(GomokuMove.create('X', 0, 3));
    state.play(GomokuMove.create('O', 3, 8));
    state.play(GomokuMove.create('X', 10, 10));
    state.play(GomokuMove.create('O', 2, 9));

    assertTrue(state.isTerminal());
    assertEquals(GameResult.LOSS, state.getResult());
  }

  @Test
  public void playHorizontalOverlap() {
    GomokuState state = new GomokuState();
    state.play(GomokuMove.create('X', 2, 3));
    state.play(GomokuMove.create('O', 0, 1));
    state.play(GomokuMove.create('X', 4, 3));
    state.play(GomokuMove.create('O', 1, 1));
    state.play(GomokuMove.create('X', 1, 3));
    state.play(GomokuMove.create('O', 2, 1));
    state.play(GomokuMove.create('X', 0, 3));
    state.play(GomokuMove.create('O', 18, 0));
    state.play(GomokuMove.create('X', 10, 10));
    state.play(GomokuMove.create('O', 17, 0));

    assertFalse(state.isTerminal());
  }

  @Test(expected = GameException.class)
  public void playNoMoveAfterEnd() {
    GomokuState state = new GomokuState();
    state.play(GomokuMove.create('X', 2, 3));
    state.play(GomokuMove.create('O', 6, 5));
    state.play(GomokuMove.create('X', 4, 3));
    state.play(GomokuMove.create('O', 5, 6));
    state.play(GomokuMove.create('X', 1, 3));
    state.play(GomokuMove.create('O', 4, 7));
    state.play(GomokuMove.create('X', 0, 3));
    state.play(GomokuMove.create('O', 3, 8));
    state.play(GomokuMove.create('X', 10, 10));
    state.play(GomokuMove.create('O', 2, 9));

    assertTrue(state.isTerminal());
    assertEquals(GameResult.LOSS, state.getResult());

    state.play(GomokuMove.create('X', 1, 1));
  }

  @Test(expected = GameException.class)
  public void playTwoMovesBySamePlayer() {
    GomokuState state = new GomokuState();
    state.play(GomokuMove.create('X', 2, 3));
    state.play(GomokuMove.create('X', 6, 5));
  }

  @Test(expected = GameException.class)
  public void playTwoMovesBySamePlace() {
    GomokuState state = new GomokuState();
    state.play(GomokuMove.create('X', 2, 3));
    state.play(GomokuMove.create('O', 2, 3));
  }

  @Test
  public void playDraw() {
    GomokuState state = new GomokuState();
    for (int i = 0; i < 19; i++) {
      int row;
      switch (i % 4) {
        case 1: row = i + 1; break;
        case 2: row = i - 1; break;
        default: row = i;
      }

      for (int j = 0; j < 19; j++) {
        if ((i + j) % 2 == 0) {
          state.play(GomokuMove.create('X', row, j));
        } else {
          state.play(GomokuMove.create('O', row, j));
        }
      }
    }

    assertTrue(state.isTerminal());
    assertEquals(GameResult.DRAW, state.getResult());
  }
}
