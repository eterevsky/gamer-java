package gamer.dominion;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class TestDominion {
  @Test
  public void create() {
    Dominion dominion1 = new Dominion.Builder(3).randomizeCards().build();
    DominionState state = dominion1.newGame();
    assertFalse(state.isTerminal());
  }
}
