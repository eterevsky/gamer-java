package gamer.def;

import java.util.Random;
import java.util.concurrent.ExecutorService;

public interface Player<G extends Game> {
  Player setTimeout(long timout);
  Player setSamplesLimit(long samplesLimit);
  Player setExecutor(ExecutorService executor, int maxWorkers);
  Player setRandom(Random random);

  Move<G> selectMove(GameState<G> state);
}
