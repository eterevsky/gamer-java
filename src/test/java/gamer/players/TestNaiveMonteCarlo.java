package gamer.players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gamer.treegame.TreeGame;
import gamer.treegame.TreeGameInstances;
import gamer.treegame.TreeGameMove;
import gamer.treegame.TreeGameState;

public final class TestNaiveMonteCarlo {

  @Test(timeout=100)
  public void play0() {
    TreeGame game = TreeGameInstances.GAME0;

    TreeGameState state = game.newGame();
    PureMonteCarlo<TreeGameState, TreeGameMove> player =
        new PureMonteCarlo<>(game);
    player.setTimeout(-1);
    player.setMaxSamples(50L);
    player.setSamplesBatch(1);
    player.setSelector(TreeGameInstances.GAME0.getRandomMoveSelector());

    TreeGameMove move = player.selectMove(state);
    assertEquals(1, move.getNodeId());
  }

  @Test(timeout=100)
  public void play1() {
    TreeGame game = TreeGameInstances.GAME1;

    TreeGameState state = game.newGame();
    PureMonteCarlo<TreeGameState, TreeGameMove> player =
        new PureMonteCarlo<>(game);
    player.setTimeout(-1);
    player.setMaxSamples(50L);
    player.setSamplesBatch(1);
    player.setSelector(TreeGameInstances.GAME1.getRandomMoveSelector());

    TreeGameMove move = player.selectMove(state);
    assertEquals(1, move.getNodeId());
    state.play(move);

    move = player.selectMove(state);
    assertEquals(3, move.getNodeId());
  }

  @Test(timeout=500)
  public void play2() {
    TreeGameState state = TreeGameInstances.GAME2.newGame();
    PureMonteCarlo<TreeGameState, TreeGameMove> player =
        new PureMonteCarlo<>(TreeGameInstances.GAME2);
    player.setTimeout(-1);
    player.setMaxSamples(50L);
    player.setSamplesBatch(1);
    player.setSelector(TreeGameInstances.GAME2.getRandomMoveSelector());

    // Unlike UCT this should generate the incorrect move.
    TreeGameMove move = player.selectMove(state);
    assertEquals(2, move.getNodeId());
    state.play(move);

    state.play(player.selectMove(state));
    state.play(player.selectMove(state));

    assertTrue(state.isTerminal());
    assertEquals(-1, state.getPayoff(0));
  }

  @Test(timeout=100)
  public void play3() {
    TreeGame game = TreeGameInstances.GAME3;

    TreeGameState state = game.newGame();
    PureMonteCarlo<TreeGameState, TreeGameMove> player =
        new PureMonteCarlo<>(game);
    player.setTimeout(-1);
    player.setMaxSamples(200L);
    player.setSamplesBatch(1);
    player.setSelector(TreeGameInstances.GAME3.getRandomMoveSelector());

    TreeGameMove move = player.selectMove(state);
    assertEquals(2, move.getNodeId());
    state.play(move);

    move = player.selectMove(state);
    assertEquals(5, move.getNodeId());
  }
}
