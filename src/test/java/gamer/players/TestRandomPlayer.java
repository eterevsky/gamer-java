package gamer.players;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import gamer.util.GameStatusInt;

import gamer.treegame.TreeGame;
import gamer.treegame.TreeGameMove;
import gamer.treegame.TreeGameState;

public final class TestRandomPlayer {

  @Test(timeout=200)
  public void play() {

    TreeGame game = TreeGame.newBuilder().setRoot(0)
        .addMove(0, 1).addMove(0, 2)
        .addMove(1, 3).addMove(2, 3)
        .addLastMove(3, 4, GameStatusInt.WIN)
        .toGame();

    TreeGameState state = game.newGame();
    RandomPlayer<TreeGameState, TreeGameMove> player = new RandomPlayer<>();

    while (!state.isTerminal()) {
      state.play(player.selectMove(state));
    }

    assertEquals(1, state.getPayoff(0));
  }
}
