package gamer.g2048;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
  public void randomGame() {
    G2048State state = G2048.getInstance().newGame();
  }
}
