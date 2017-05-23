package gamer.mcts;

import gamer.treegame.TreeGameInstances;
import gamer.treegame.TreeGameMove;
import gamer.treegame.TreeGameState;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class MonteCarloPlayerTest {

  @Test
  public void testAttributes() {
    MonteCarloPlayer<TreeGameState, TreeGameMove> player =
        new MonteCarloPlayer<>(TreeGameInstances.GAME0);
    assertNotNull(player.getName());
  }

  @Test
  public void testGame0() {
    TreeGameState state0 = TreeGameInstances.GAME0.newGame();
    MonteCarloPlayer<TreeGameState, TreeGameMove> player =
        new MonteCarloPlayer<>(TreeGameInstances.GAME0);
    player.setMaxSamples(50);

    assertEquals(TreeGameInstances.GAME0.getMove(1), player.selectMove(state0));

    System.out.println(player.getReport());
  }

  @Test
  public void testGame1() {
    TreeGameState state0 = TreeGameInstances.GAME1.newGame();
    MonteCarloPlayer<TreeGameState, TreeGameMove> player =
        new MonteCarloPlayer<>(TreeGameInstances.GAME1);
    player.setMaxSamples(50);

    assertEquals(TreeGameInstances.GAME1.getMove(1), player.selectMove(state0));

//    System.out.println(player.getReport());
  }

  @Test
  public void testGame2() {
    TreeGameState state0 = TreeGameInstances.GAME2.newGame();
    MonteCarloPlayer<TreeGameState, TreeGameMove> player =
        new MonteCarloPlayer<>(TreeGameInstances.GAME2);
    player.setMaxSamples(100);
    TreeGameMove move = player.selectMove(state0);
//    System.out.println(player.getReport());

    assertEquals(TreeGameInstances.GAME2.getMove(1), move);
  }

  @Test
  public void testGame3() {
    TreeGameState state0 = TreeGameInstances.GAME3.newGame();
    MonteCarloPlayer<TreeGameState, TreeGameMove> player =
        new MonteCarloPlayer<>(TreeGameInstances.GAME3);
    player.setMaxSamples(100);
    TreeGameMove move = player.selectMove(state0);
//    System.out.println(player.getReport());

    assertEquals(TreeGameInstances.GAME3.getMove(2), move);
  }

  @Test
  public void testGame4() {
    TreeGameState state0 = TreeGameInstances.GAME4.newGame();
    MonteCarloPlayer<TreeGameState, TreeGameMove> player =
        new MonteCarloPlayer<>(TreeGameInstances.GAME4);
    player.setMaxSamples(100);
    TreeGameMove move = player.selectMove(state0);
//    System.out.println(player.getReport());

    assertFalse(TreeGameInstances.GAME4.getMove(3) == move);
  }
}
