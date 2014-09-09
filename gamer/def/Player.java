package gamer.def;

import java.util.Random;
import java.util.concurrent.ExecutorService;

public interface Player<G extends Game> {
  Player<G> setTimeout(long timout);
  Player<G> setSamplesLimit(long samplesLimit);
  Player<G> setExecutor(ExecutorService executor, int maxWorkers);
  Player<G> setRandom(Random random);
  Player<G> setSamplesBatch(int samplesBatch);
  Player<G> setName(String name);

  String getName();

  Move<G> selectMove(GameState<G> state);
}
