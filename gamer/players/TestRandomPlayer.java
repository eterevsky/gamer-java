package gamer.players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gamer.def.GameResult;
import gamer.treegame.TreeGame;
import gamer.treegame.TreeGameState;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

public class TestRandomPlayer {

  @Test
  public void play() {

    TreeGame game = TreeGame.newBuilder().setRoot(0)
            .addNode(0, true)
            .addNode(1, false).addNode(2, false)
            .addNode(3, true)
            .addTermNode(4, false, GameResult.WIN)
            .addMove(0, 1).addMove(0, 2)
            .addMove(1, 3).addMove(2, 3)
            .addMove(3, 4)
            .toGame();

    TreeGameState state = game.newGame();
    RandomPlayer<TreeGame> player = new RandomPlayer<TreeGame>();

    while (!state.isTerminal()) {
      state.play(player.selectMove(state));
    }

    assertTrue(state.isTerminal());
    assertEquals(GameResult.WIN, state.getResult());
  }
}
