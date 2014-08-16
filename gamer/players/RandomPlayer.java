package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomPlayer<G extends Game> implements Player<G> {
  private Random random = null;

  public Move<G> selectMove(GameState<G> state) {
    return state.getRandomMove(
        random == null ? ThreadLocalRandom.current() : random);
  }

  public RandomPlayer<G> setTimeout(long timeout) {
    return this;
  }

  public RandomPlayer<G> setSamplesLimit(long samplesLimit) {
    return this;
  }

  public RandomPlayer<G> setSamplesBatch(int samplesBatch) {
    return this;
  }

  public RandomPlayer<G> setExecutor(ExecutorService executor, int maxWorkers) {
    return this;
  }

  public RandomPlayer<G> setRandom(Random random) {
    this.random = random;
    return this;
  }
}
