package gamer.players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gamer.def.GameResult;
import gamer.treegame.TreeGame;
import gamer.treegame.TreeGameMove;
import gamer.treegame.TreeGameInstances;
import gamer.treegame.TreeGameState;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

public class TestMonteCarloUcb {

  @Test
  public void play0() throws Exception {
    TreeGame game = TreeGameInstances.GAME0;

    TreeGameState state = game.newGame();
    MonteCarloUcb<TreeGame> player = new MonteCarloUcb<>();
    player.setTimeout(-1).setSamplesLimit(50L);

    TreeGameMove move = (TreeGameMove) player.selectMove(state);
    assertEquals(1, move.getNodeId());
  }

  @Test
  public void play1() throws Exception {
    TreeGame game = TreeGameInstances.GAME1;

    TreeGameState state = game.newGame();
    MonteCarloUcb<TreeGame> player = new MonteCarloUcb<>();
    player.setTimeout(-1).setSamplesLimit(50L);

    TreeGameMove move = (TreeGameMove) player.selectMove(state);
    assertEquals(1, move.getNodeId());
    state.play(move);

    move = (TreeGameMove) player.selectMove(state);
    assertEquals(3, move.getNodeId());
  }

  @Test
  public void play3() throws Exception {
    TreeGame game = TreeGameInstances.GAME3;

    TreeGameState state = game.newGame();
    MonteCarloUcb<TreeGame> player = new MonteCarloUcb<>();
    player.setTimeout(-1).setSamplesLimit(50L);

    TreeGameMove move = (TreeGameMove) player.selectMove(state);
    assertEquals(2, move.getNodeId());
    state.play(move);

    move = (TreeGameMove) player.selectMove(state);
    assertEquals(5, move.getNodeId());
  }
}
