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

    assertEquals(4, state.get("B2"));
    assertEquals(0, state.getPlayer());

    state.play("right");
  }
}
