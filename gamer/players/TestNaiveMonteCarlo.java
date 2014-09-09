package gamer.players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gamer.def.GameStatus;
import gamer.treegame.TreeGame;
import gamer.treegame.TreeGameMove;
import gamer.treegame.TreeGameInstances;
import gamer.treegame.TreeGameState;

import java.util.Random;

import org.junit.Test;

public final class TestNaiveMonteCarlo {

  @Test(timeout=100)
  public void play0() {
    TreeGame game = TreeGameInstances.GAME0;

    TreeGameState state = game.newGame();
    NaiveMonteCarlo<TreeGame> player = new NaiveMonteCarlo<>();
    player.setTimeout(-1).setSamplesLimit(50L).setSamplesBatch(1);

    TreeGameMove move = (TreeGameMove) player.selectMove(state);
    assertEquals(1, move.getNodeId());
  }

  @Test(timeout=100)
  public void play1() {
    TreeGame game = TreeGameInstances.GAME1;

    TreeGameState state = game.newGame();
    NaiveMonteCarlo<TreeGame> player = new NaiveMonteCarlo<>();
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
    NaiveMonteCarlo<TreeGame> player = new NaiveMonteCarlo<>();
    player.setTimeout(-1).setSamplesLimit(50L).setSamplesBatch(1)
          .setRandom(new Random(1234567890L));

    // Unlike UCT this should generate the incorrect move.
    TreeGameMove move = (TreeGameMove) player.selectMove(state);
    assertEquals(2, move.getNodeId());
    state = state.play(move);

    state = state.play(player.selectMove(state));
    state = state.play(player.selectMove(state));

    assertTrue(state.isTerminal());
    assertEquals(GameStatus.LOSS, state.status());
  }

  @Test(timeout=100)
  public void play3() {
    TreeGame game = TreeGameInstances.GAME3;

    TreeGameState state = game.newGame();
    NaiveMonteCarlo<TreeGame> player = new NaiveMonteCarlo<>();
    player.setTimeout(-1).setSamplesLimit(50L).setSamplesBatch(1)
          .setRandom(new Random(1234567890L));

    TreeGameMove move = (TreeGameMove) player.selectMove(state);
    assertEquals(2, move.getNodeId());
    state = state.play(move);

    move = (TreeGameMove) player.selectMove(state);
    assertEquals(5, move.getNodeId());
  }
}
