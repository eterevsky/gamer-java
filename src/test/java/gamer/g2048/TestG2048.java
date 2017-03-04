package gamer.g2048;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestG2048 {
  @Test
  public void simpleGame() {
    G2048State state = G2048.getInstance().newGame();
    assertEquals(-1, state.getPlayer());
    state.play("A1 2");
    assertEquals(-1, state.getPlayer());
    state.play("B2 4");

    assertEquals(1, state.get("A1"));
    assertEquals(0, state.get("A2"));
    assertEquals(0, state.get("B1"));
    assertEquals(2, state.get("B2"));
    assertEquals(0, state.get("C3"));
    assertEquals(0, state.getPlayer());
    assertEquals(0, state.score);

    state.play("right");

    assertEquals(-1, state.getPlayer());
    assertEquals(0, state.get("A1"));
    assertEquals(1, state.get("D1"));
    assertEquals(0, state.get("B2"));
    assertEquals(2, state.get("D2"));
    assertEquals(0, state.score);

    state.play("d4 4");

    assertEquals(0, state.getPlayer());

    state.play("up");

    assertEquals(3, state.get("d4"));
    assertEquals(1, state.get("d3"));
    assertEquals(8, state.score);
  }

  @Test
  public void validMoves() {
    G2048State state = G2048.getInstance().newGame();
    List<G2048Move> moves = state.getMoves();

    assertEquals(32, moves.size());

    state.play("A1 2");

    moves = state.getMoves();
    assertEquals(30, moves.size());
    for (G2048Move move : moves) {
      assertFalse(move == G2048Move.parse("A1 2"));
      assertFalse(move == G2048Move.parse("A1 4"));
    }

    for (int i = 0; i < 100; i++) {
      G2048Move move = state.getRandomMove();
      assertFalse(move == G2048Move.parse("A1 2"));
      assertFalse(move == G2048Move.parse("A1 4"));
    }

    state.play("B2 4");

    moves = state.getMoves();
    assertEquals(4, moves.size());

    state.play("up");

    moves = state.getMoves();
    assertEquals(28, moves.size());
    for (G2048Move move : moves) {
      assertFalse(move == G2048Move.parse("A4 2"));
      assertFalse(move == G2048Move.parse("A4 4"));
      assertFalse(move == G2048Move.parse("B4 2"));
      assertFalse(move == G2048Move.parse("B4 4"));
    }

    for (int i = 0; i < 100; i++) {
      G2048Move move = state.getRandomMove();
      assertFalse(move == G2048Move.parse("A4 2"));
      assertFalse(move == G2048Move.parse("A4 4"));
      assertFalse(move == G2048Move.parse("B4 2"));
      assertFalse(move == G2048Move.parse("B4 4"));
    }
  }

  @Test(timeout = 100)
  public void randomGame() {
    G2048State state = G2048.getInstance().newGame();
    int i = 0;
    while (!state.isTerminal()) {
      assertTrue(state.getMoves().size() > 0);
      state.play(state.getRandomMove());
      i++;
    }

    assertTrue(i >= 32);
    assertTrue(state.getPayoff(0) >= i);
  }
}
