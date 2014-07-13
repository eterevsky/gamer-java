package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class RandomSampleEvaluator<G extends Game> implements Evaluator<G> {
  private final int nsamples;
  private final Random random;

  RandomSampleEvaluator(int nsamples, Random random) {
    this.nsamples = nsamples;
    this.random = random;
  }

  public double evaluate(GameState<G> origState) {
    if (origState.isTerminal()) {
      return nsamples * origState.status().value();
    }

    Random rnd = random == null ? ThreadLocalRandom.current() : random;
    double s = 0;

    for (int i = 0; i < nsamples; i++) {
      GameState<G> state = origState;
      while (!state.isTerminal()) {
        state = state.play(state.getRandomMove(rnd));
      }

      s += state.status().value();
    }

    return s;
  }

  @Override
  public RandomSampleEvaluator<G> clone() {
    return new RandomSampleEvaluator<G>(nsamples, random);
  }
}
