package gamer.treegame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Random;

public class TestTreeGame {

  @Test(timeout=50)
  public void simpleGame() {
    Random random = new Random(1234567890L);
    TreeGame game = TreeGameInstances.GAME1;

    TreeGameState state = game.newGame();
    assertEquals(2, state.getMoves().size());
    assertFalse(state.isTerminal());
    assertTrue(state.getPlayerBool());

    state = state.play(new TreeGameMove(game.getNode(1)));
    assertFalse(state.isTerminal());
    assertFalse(state.getPlayerBool());
    assertEquals(1, state.getMoves().size());

    state = state.play(state.getRandomMove(random));  // Only 1 move available.
    assertFalse(state.isTerminal());
    assertTrue(state.getPlayerBool());

    state = state.play(state.getMoves().get(0));
    assertTrue(state.isTerminal());
    assertTrue(state.getPayoff(0) > 0);

    // Another game with the same tree.
    state = game.newGame();

    state = state.play(new TreeGameMove(game.getNode(2)));
    assertFalse(state.isTerminal());
    assertFalse(state.getPlayerBool());
    assertEquals(2, state.getMoves().size());

    state = state.play(new TreeGameMove(game.getNode(4)));
    assertTrue(state.isTerminal());
    assertTrue(state.getPayoff(0) < 0);
  }
}
