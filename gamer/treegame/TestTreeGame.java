package gamer.treegame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gamer.def.GameException;
import gamer.def.GameResult;
import gamer.def.Move;

import java.util.List;

import org.junit.Test;

public class TestTreeGame {

  @Test
  public void simpleGame() {
    TreeGame game = TreeGameInstances.GAME1;

    TreeGameState state = game.newGame();
    List<Move<TreeGame>> moves = state.getMoves();
    assertEquals(2, state.getMoves().size());
    assertFalse(state.isTerminal());
    assertTrue(state.getPlayer());

    state.play(new TreeGameMove(game.getNode(1)));
    assertFalse(state.isTerminal());
    assertFalse(state.getPlayer());
    assertEquals(1, state.getMoves().size());

    state.play(state.getRandomMove());  // Only 1 move available.
    assertFalse(state.isTerminal());
    assertTrue(state.getPlayer());

    state.play(state.getMoves().get(0));
    assertTrue(state.isTerminal());
    assertEquals(GameResult.WIN, state.getResult());

    // Another game with the same tree.
    state = game.newGame();

    state.play(new TreeGameMove(game.getNode(2)));
    assertFalse(state.isTerminal());
    assertFalse(state.getPlayer());
    assertEquals(2, state.getMoves().size());

    state.play(new TreeGameMove(game.getNode(4)));
    assertTrue(state.isTerminal());
    assertEquals(GameResult.LOSS, state.getResult());
  }
}
