package gamer.players;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import gamer.treegame.TreeGame;
import gamer.treegame.TreeGameInstances;
import gamer.treegame.TreeGameMove;
import gamer.treegame.TreeGameState;

import java.util.Random;

public final class TestMonteCarloUcb {

  @Test(timeout=1000)
  public void play0() {
    TreeGame game = TreeGameInstances.GAME0;

    TreeGameState state = game.newGame();
    MonteCarloUcb<TreeGameState, TreeGameMove> player = new MonteCarloUcb<>();
    player.setTimeout(-1);
    player.setMaxSamples(50L);
    player.setSamplesBatch(1);

    TreeGameMove move = player.selectMove(state);
    assertEquals(1, move.getNodeId());
  }

  @Test(timeout=500)
  public void play1() {
    TreeGame game = TreeGameInstances.GAME1;

    TreeGameState state = game.newGame();
    MonteCarloUcb<TreeGameState, TreeGameMove> player = new MonteCarloUcb<>();
    player.setTimeout(-1);
    player.setMaxSamples(50L);
    player.setSamplesBatch(1);

    TreeGameMove move = player.selectMove(state);
    assertEquals(1, move.getNodeId());
    state = state.play(move);

    move = player.selectMove(state);
    assertEquals(3, move.getNodeId());
  }

  @Test(timeout=500)
  public void play3() {
    TreeGame game = TreeGameInstances.GAME3;

    TreeGameState state = game.newGame();
    MonteCarloUcb<TreeGameState, TreeGameMove> player = new MonteCarloUcb<>();
    player.setTimeout(-1);
    player.setMaxSamples(50L);
    player.setSamplesBatch(1);
    player.setRandom(new Random(1234567890L));

    TreeGameMove move = player.selectMove(state);
    assertEquals(2, move.getNodeId());
    state = state.play(move);

    move = player.selectMove(state);
    assertEquals(5, move.getNodeId());
  }
}
