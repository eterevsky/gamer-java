package gamer.players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gamer.def.GameStatus;
import gamer.treegame.TreeGame;
import gamer.treegame.TreeGameMove;
import gamer.treegame.TreeGameInstances;
import gamer.treegame.TreeGameState;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

public final class TestMonteCarloUct {

  @Test(timeout=100)
  public void play0() {
    TreeGameState state = TreeGameInstances.GAME0.newGame();
    MonteCarloUct<TreeGame> player = new MonteCarloUct<>();
    player.setTimeout(-1).setSamplesLimit(50L).setSamplesBatch(1);

    TreeGameMove move = (TreeGameMove) player.selectMove(state);
    assertEquals(1, move.getNodeId());
  }

  @Test(timeout=100)
  public void play1() {
    TreeGameState state = TreeGameInstances.GAME1.newGame();
    MonteCarloUct<TreeGame> player = new MonteCarloUct<>();
    player.setTimeout(-1).setSamplesLimit(50L).setSamplesBatch(1);

    TreeGameMove move = (TreeGameMove) player.selectMove(state);
    assertEquals(1, move.getNodeId());
    state = state.play(move);

    move = (TreeGameMove) player.selectMove(state);
    assertEquals(3, move.getNodeId());
  }

  @Test(timeout=500)
  public void play2() {
    TreeGameState state = TreeGameInstances.GAME2.newGame();
    MonteCarloUct<TreeGame> player = new MonteCarloUct<>();
    player.setTimeout(-1).setSamplesLimit(500L).setSamplesBatch(1);

    TreeGameMove move = (TreeGameMove) player.selectMove(state);
    assertEquals(1, move.getNodeId());
    state = state.play(move);

    state = state.play(player.selectMove(state));
    state = state.play(player.selectMove(state));

    assertTrue(state.isTerminal());
    assertEquals(GameStatus.WIN, state.status());
  }

  @Test(timeout=100)
  public void play3() {
    TreeGameState state = TreeGameInstances.GAME3.newGame();
    MonteCarloUct<TreeGame> player = new MonteCarloUct<>();
    player.setTimeout(-1).setSamplesLimit(50L).setSamplesBatch(1);

    TreeGameMove move = (TreeGameMove) player.selectMove(state);
    assertEquals(2, move.getNodeId());
    state = state.play(move);

    move = (TreeGameMove) player.selectMove(state);
    assertEquals(5, move.getNodeId());
  }
}
