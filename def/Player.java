package gamer.def;

import java.util.concurrent.ExecutorService;

public interface Player<G extends Game> {
  Player setTimeout(double timoutInSec);
  Player setExecutor(ExecutorService executor, int maxWorkers);

  Move<G> selectMove(GameState<G> state) throws Exception;
}
