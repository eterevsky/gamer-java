package gomoku;

import gamer.GameResult;

import static org.junit.Assert.assertEquals;
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
}
