package gamer.treegame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gamer.def.GameException;
import gamer.def.GameStatus;
import gamer.def.Move;

import java.util.List;
import java.util.Random;

import org.junit.Test;

public class TestTreeGame {

  @Test(timeout=50)
  public void simpleGame() {
    Random random = new Random(1234567890L);
    TreeGame game = TreeGameInstances.GAME1;

    TreeGameState state = game.newGame();
    List<Move<TreeGame>> moves = state.getMoves();
    assertEquals(2, state.getMoves().size());
    assertFalse(state.isTerminal());
    assertTrue(state.status().getPlayer());

    state = state.play(new TreeGameMove(game.getNode(1)));
    assertFalse(state.isTerminal());
    assertFalse(state.status().getPlayer());
    assertEquals(1, state.getMoves().size());

    state = state.play(state.getRandomMove(random));  // Only 1 move available.
    assertFalse(state.isTerminal());
    assertTrue(state.status().getPlayer());

    state = state.play(state.getMoves().get(0));
    assertTrue(state.isTerminal());
    assertEquals(GameStatus.WIN, state.status());

    // Another game with the same tree.
    state = game.newGame();

    state = state.play(new TreeGameMove(game.getNode(2)));
    assertFalse(state.isTerminal());
    assertFalse(state.status().getPlayer());
    assertEquals(2, state.getMoves().size());

    state = state.play(new TreeGameMove(game.getNode(4)));
    assertTrue(state.isTerminal());
    assertEquals(GameStatus.LOSS, state.status());
  }
}
