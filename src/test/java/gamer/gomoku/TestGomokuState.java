package gamer.gomoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gamer.def.GameException;
import gamer.def.GameStatus;

import java.util.Random;
import org.junit.Test;

public class TestGomokuState {
  private void testPlayGame(String gameStr, GameStatus result) {
    GomokuState state = Gomoku.getInstance().newGame();
    boolean player = true;

    for (String moveStr : gameStr.split(" ")) {
      assertEquals(player, state.status().getPlayer());
      assertFalse(state.isTerminal());
      state = state.play(GomokuMove.of(moveStr));
      player = !player;
    }

    assertTrue(state.isTerminal());
    assertEquals(result, state.status());
  }

  @Test(timeout=50)
  public void playVertical() {
    testPlayGame("c3 d3 c4 b4 c5 c2 c6 e6 c7", GameStatus.WIN);
  }

  @Test(timeout=50)
  public void playHorizontal() {
    testPlayGame("c3 d3 c4 b4 c5 c2 c6 e6 c7", GameStatus.WIN);
  }

  @Test(timeout=50)
  public void playDiagonal1() {
    testPlayGame("e5 f4 f5 g5 e3 e6 f6 h6 g7 h8 h7 k8 d4 j7", GameStatus.LOSS);
  }

  @Test(timeout=50)
  public void playDiagonal2() {
    testPlayGame(
        "e6 e7 f7 f6 d8 e8 e9 d7 f8 g5 g7 h6 d10 c7 c11", GameStatus.WIN);
  }

  @Test(timeout=50)
  public void playHorizontalOverlap() {
    GomokuState state = new GomokuState();
    state = state.play(GomokuMove.of(2, 3));
    state = state.play(GomokuMove.of(0, 1));
    state = state.play(GomokuMove.of(4, 3));
    state = state.play(GomokuMove.of(1, 1));
    state = state.play(GomokuMove.of(1, 3));
    state = state.play(GomokuMove.of(2, 1));
    state = state.play(GomokuMove.of(0, 3));
    state = state.play(GomokuMove.of(Gomoku.SIZE - 1, 0));
    state = state.play(GomokuMove.of(10, 10));
    state = state.play(GomokuMove.of(Gomoku.SIZE - 2, 0));

    assertFalse(state.isTerminal());
  }

  @Test(timeout=50)
  public void playBorders() {
    if (Gomoku.SIZE != 19)
      return;
    testPlayGame(
        "q10 q11 r10 r11 s10 s11 t10 t11 a11 a10 b11 b10 c11 c10 d11 d10 " +
        "c12 c9 b13 b8 a14 a7 t15 t6 s16 s5 r17 r4 q18 q3 p19",
        GameStatus.WIN);
  }

  @Test(expected = GameException.class, timeout=50)
  public void playNoMoveAfterEnd() {
    GomokuState state = new GomokuState();
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

    assertEquals(GameStatus.LOSS, state.status());

    state = state.play(GomokuMove.of(1, 1));
  }

  @Test(expected = GameException.class, timeout=50)
  public void playTwoMovesBySamePlace() {
    GomokuState state = new GomokuState();
    state = state.play(GomokuMove.of(2, 3));
    state = state.play(GomokuMove.of(2, 3));
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
          state = state.play(GomokuMove.of(row, j));
        } else {
          state = state.play(GomokuMove.of(row, j));
        }
      }
    }

    assertTrue(state.isTerminal());
    assertEquals(GameStatus.DRAW, state.status());
  }

  @Test(timeout=100)
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
    assertTrue(moves <= Gomoku.POINTS);
  }
}
