package gamer;

import java.util.concurrent.ExecutorService;

public interface Player {
  Player setTimeout(double timoutInSec);
  Player setExecutor(ExecutorService executor, int maxWorkers);

  <T extends Game> Move<T> selectMove(GameState<T> state)
      throws Exception;
}
