package gamer.minimax;

import gamer.treegame.TreeGameInstances;
import gamer.treegame.TreeGameMove;
import gamer.treegame.TreeGameState;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class MinimaxTest {

  @Test
  public void testAttributes() {
    MinimaxPlayer<TreeGameState, TreeGameMove> player =
        new MinimaxPlayer<>();
    assertNotNull(player.getName());
  }

  @Test
  public void testGame0() {
    TreeGameState state0 = TreeGameInstances.GAME0.newGame();
    MinimaxPlayer<TreeGameState, TreeGameMove> player = new MinimaxPlayer<>();
    player.setEvaluator(new TerminalEvaluator<>());
    player.setMaxDepth(1);
    assertEquals(TreeGameInstances.GAME0.getMove(1), player.selectMove(state0));

    player.setMaxDepth(2);
    assertEquals(TreeGameInstances.GAME0.getMove(1), player.selectMove(state0));

    player.setMaxDepth(3);
    assertEquals(TreeGameInstances.GAME0.getMove(1), player.selectMove(state0));
  }

  @Test
  public void testGame1() {
    TreeGameState state = TreeGameInstances.GAME1.newGame();
    MinimaxPlayer<TreeGameState, TreeGameMove> player = new MinimaxPlayer<>();
    player.setEvaluator(new TerminalEvaluator<>());
    player.setMaxDepth(2);
    assertEquals(TreeGameInstances.GAME1.getMove(1), player.selectMove(state));

    player.setMaxDepth(3);
    assertEquals(TreeGameInstances.GAME1.getMove(1), player.selectMove(state));

    player.setMaxDepth(4);
    assertEquals(TreeGameInstances.GAME1.getMove(1), player.selectMove(state));

    state.play(TreeGameInstances.GAME1.getMove(1));
    assertEquals(TreeGameInstances.GAME1.getMove(3), player.selectMove(state));
    state.play(TreeGameInstances.GAME1.getMove(3));
    assertEquals(TreeGameInstances.GAME1.getMove(5), player.selectMove(state));
  }

  @Test
  public void testGame2() {
    TreeGameState state = TreeGameInstances.GAME2.newGame();
    MinimaxPlayer<TreeGameState, TreeGameMove> player = new MinimaxPlayer<>();
    player.setEvaluator(new TerminalEvaluator<>());
    player.setMaxDepth(3);
    assertEquals(TreeGameInstances.GAME2.getMove(1), player.selectMove(state));
    player.setMaxDepth(4);
    assertEquals(TreeGameInstances.GAME2.getMove(1), player.selectMove(state));

    state.play(TreeGameInstances.GAME2.getMove(1));
    state.play(TreeGameInstances.GAME2.getMove(3));
    assertEquals(TreeGameInstances.GAME2.getMove(8), player.selectMove(state));
  }

  @Test
  public void testGame3() {
    TreeGameState state = TreeGameInstances.GAME3.newGame();
    MinimaxPlayer<TreeGameState, TreeGameMove> player = new MinimaxPlayer<>();
    player.setEvaluator(new TerminalEvaluator<>());
    player.setMaxDepth(2);
    assertEquals(TreeGameInstances.GAME3.getMove(2), player.selectMove(state));
    player.setMaxDepth(3);
    assertEquals(TreeGameInstances.GAME3.getMove(2), player.selectMove(state));

    state.play(TreeGameInstances.GAME3.getMove(2));
    player.setMaxDepth(1);
    assertEquals(TreeGameInstances.GAME3.getMove(5), player.selectMove(state));
    player.setMaxDepth(2);
    assertEquals(TreeGameInstances.GAME3.getMove(5), player.selectMove(state));

    state.play(TreeGameInstances.GAME3.getMove(5));
    assertEquals(TreeGameInstances.GAME3.getMove(8), player.selectMove(state));
  }

  @Test
  public void testGame4() {
    TreeGameState state = TreeGameInstances.GAME4.newGame();
    MinimaxPlayer<TreeGameState, TreeGameMove> player = new MinimaxPlayer<>();
    player.setEvaluator(new TerminalEvaluator<>());
    player.setMaxDepth(5);
    assertEquals(TreeGameInstances.GAME4.getMove(2), player.selectMove(state));

    state.play(TreeGameInstances.GAME4.getMove(2));
    assertEquals(TreeGameInstances.GAME4.getMove(4), player.selectMove(state));

    state.play(TreeGameInstances.GAME4.getMove(4));
    assertEquals(TreeGameInstances.GAME4.getMove(7), player.selectMove(state));
  }
}
