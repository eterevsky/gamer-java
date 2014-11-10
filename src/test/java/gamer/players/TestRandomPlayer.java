package gamer.players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gamer.def.GameStatus;
import gamer.treegame.TreeGame;
import gamer.treegame.TreeGameState;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

public final class TestRandomPlayer {

  @Test(timeout=200)
  public void play() {

    TreeGame game = TreeGame.newBuilder().setRoot(0)
        .addMove(0, 1).addMove(0, 2)
        .addMove(1, 3).addMove(2, 3)
        .addLastMove(3, 4, GameStatus.WIN)
        .toGame();

    TreeGameState state = game.newGame();
    RandomPlayer<TreeGame> player = new RandomPlayer<TreeGame>();

    while (!state.isTerminal()) {
      state = state.play(player.selectMove(state));
    }

    assertEquals(GameStatus.WIN, state.status());
  }
}
