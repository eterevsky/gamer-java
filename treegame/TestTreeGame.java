package treegame;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gamer.GameException;
import gamer.GameResult;
import gamer.Move;

import java.util.List;

import org.junit.Test;

public class TestTreeGame {

  @Test
  public void simpleGame() {
    TreeGame game =
        TreeGame.newBuilder().setRoot(0)
            .addNode(0, true)
            .addNode(1, false).addNode(2, false)
            .addNode(3, true).addTermNode(4, true, GameResult.LOSS)
            .addTermNode(5, false, GameResult.WIN)
            .addMove(0, 1).addMove(0, 2)
            .addMove(1, 3).addMove(2, 3).addMove(2, 4)
            .addMove(3, 5)
            .toGame();

    TreeGameState state = game.newGame();
    List<Move<TreeGame>> moves = state.getAvailableMoves();
    assertEquals(2, state.getAvailableMoves().size());
    assertFalse(state.isTerminal());
    assertTrue(state.getPlayer());

    state.play(new TreeGameMove(game.getNode(1)));
    assertFalse(state.isTerminal());
    assertFalse(state.getPlayer());
    assertEquals(1, state.getAvailableMoves().size());

    state.play(state.getRandomMove());  // Only 1 move available.
    assertFalse(state.isTerminal());
    assertTrue(state.getPlayer());

    state.play(state.getAvailableMoves().get(0));
    assertTrue(state.isTerminal());
    assertEquals(GameResult.WIN, state.getResult());

    // Another game with the same tree.
    state = game.newGame();

    state.play(new TreeGameMove(game.getNode(2)));
    assertFalse(state.isTerminal());
    assertFalse(state.getPlayer());
    assertEquals(2, state.getAvailableMoves().size());

    state.play(new TreeGameMove(game.getNode(4)));
    assertTrue(state.isTerminal());
    assertEquals(GameResult.LOSS, state.getResult());
  }
}
