package players;

import gamer.Game;
import gamer.GameState;
import gamer.Move;
import gamer.Player;

import java.util.concurrent.ExecutorService;

public class RandomPlayer implements Player {
  public <T extends Game> Move<T> selectMove(GameState<T> state) {
    return state.getRandomMove();
  }

  public RandomPlayer setTimeout(double timeoutInSec) {
    return this;
  }

  public MonteCarloUcb setExecutor(Executor executor, int maxWorkers) {
    return this;
  }
}
