package players;

import gamer.Game;
import gamer.GameState;
import gamer.Move;
import gamer.Player;

import java.util.concurrent.ExecutorService;

public class RandomPlayer<G extends Game> implements Player<G> {
  public Move<G> selectMove(GameState<G> state) {
    return state.getRandomMove();
  }

  public RandomPlayer setTimeout(double timeoutInSec) {
    return this;
  }

  public RandomPlayer setExecutor(ExecutorService executor, int maxWorkers) {
    return this;
  }
}
