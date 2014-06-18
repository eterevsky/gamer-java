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
}
