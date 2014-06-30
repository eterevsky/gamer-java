package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;

import java.util.concurrent.ExecutorService;

public class RandomPlayer<G extends Game> implements Player<G> {
  public Move<G> selectMove(GameState<G> state) {
    return state.getRandomMove();
  }

  public RandomPlayer setTimeout(double timeoutInSec) {
    return this;
  }

  public RandomPlayer setSamplesLimit(long samplesLimit) {
    return this;
  }

  public RandomPlayer setExecutor(ExecutorService executor, int maxWorkers) {
    return this;
  }
}
