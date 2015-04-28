package gamer.gomoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gamer.def.GameException;

import java.util.Random;

public class TestGomokuState {
  private void testPlayGame(String gameStr, int expectedPayoff) {
    GomokuState state = Gomoku.getInstance().newGame();
    boolean player = true;

    for (String moveStr : gameStr.split(" ")) {
      assertEquals(player, state.getPlayerBool());
      assertFalse(state.isTerminal());
      state = state.play(GomokuMove.of(moveStr));
      player = !player;
    }

    assertTrue(state.isTerminal());
    assertEquals(expectedPayoff, state.getPayoff(0));
  }

  @Test(timeout=50)
  public void playVertical() {
    testPlayGame("c3 d3 c4 b4 c5 c2 c6 e6 c7", 1);
  }

  @Test(timeout=50)
  public void playHorizontal() {
    testPlayGame("c3 d3 c4 b4 c5 c2 c6 e6 c7", 1);
  }

  @Test(timeout=50)
  public void playDiagonal1() {
    testPlayGame("e5 f4 f5 g5 e3 e6 f6 h6 g7 h8 h7 k8 d4 j7", -1);
  }

  @Test(timeout=50)
  public void playDiagonal2() {
    testPlayGame(
        "e6 e7 f7 f6 d8 e8 e9 d7 f8 g5 g7 h6 d10 c7 c11", 1);
  }

  @Test(timeout=50)
  public void playBorders() {
    testPlayGame(
        "q10 q11 r10 r11 s10 s11 t10 t11 a11 a10 b11 b10 c11 c10 d11 d10 " +
        "c12 c9 b13 b8 a14 a7 t15 t6 s16 s5 r17 r4 q18 q3 p19",
        1);
  }

  @Test(timeout=50)
  public void playHorizontalOverlap() {
    GomokuState state = Gomoku.getInstance().newGame();
    state = state.play(GomokuMove.of(2, 3));
    state = state.play(GomokuMove.of(0, 1));
    state = state.play(GomokuMove.of(4, 3));
    state = state.play(GomokuMove.of(1, 1));
    state = state.play(GomokuMove.of(1, 3));
    state = state.play(GomokuMove.of(2, 1));
    state = state.play(GomokuMove.of(0, 3));
    state = state.play(GomokuMove.of(18, 0));
    state = state.play(GomokuMove.of(10, 10));
    state = state.play(GomokuMove.of(17, 0));

    assertFalse(state.isTerminal());
  }

  @Test(expected = GameException.class, timeout=50)
  public void playNoMoveAfterEnd() {
    GomokuState state = Gomoku.getInstance().newGame();
    state = state.play(GomokuMove.of(2, 3));
    state = state.play(GomokuMove.of(6, 5));
    state = state.play(GomokuMove.of(4, 3));
    state = state.play(GomokuMove.of(5, 6));
    state = state.play(GomokuMove.of(1, 3));
    state = state.play(GomokuMove.of(4, 7));
    state = state.play(GomokuMove.of(0, 3));
    state = state.play(GomokuMove.of(3, 8));
    state = state.play(GomokuMove.of(10, 10));
    state = state.play(GomokuMove.of(2, 9));

    assertEquals(-1, state.getPayoff(0));

    state = state.play(GomokuMove.of(1, 1));
  }

  @Test(expected = GameException.class, timeout=50)
  public void playTwoMovesBySamePlace() {
    GomokuState state = Gomoku.getInstance().newGame();
    state = state.play(GomokuMove.of(2, 3));
    state = state.play(GomokuMove.of(2, 3));
  }

  @Test(timeout=100)
  public void playDraw() {
		Gomoku gomoku = Gomoku.getInstance();
    GomokuState state = gomoku.newGame();
    for (int i = 0; i < gomoku.getSize(); i++) {
      int row;
      switch (i % 4) {
        case 1: row = i + 1; break;
        case 2: row = i - 1; break;
        default: row = i;
      }

      for (int j = 0; j < gomoku.getSize(); j++) {
        state = state.play(GomokuMove.of(row, j));
      }
    }

    assertTrue(state.isTerminal());
    assertEquals(0, state.getPayoff(0));
  }

  @Test(timeout=100)
  public void playRandomMoves() {
		Gomoku gomoku = Gomoku.getInstance();
    GomokuState state = gomoku.newGame();
    Random random = new Random(1234567890L);

    assertTrue(state.getPlayerBool());
    int moves = 0;

    while (!state.isTerminal()) {
      state = state.play(state.getRandomMove(random));
      moves++;
    }

    assertTrue(moves >= 9);
    assertTrue(moves <= gomoku.getPoints());
  }
}
