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
    int s = 0;

    if (origState.isTerminal()) {
      return nsamples * origState.getResult().asDouble();
    }

    for (int i = 0; i < nsamples; i++) {
      GameState<G> state = origState.clone();
      while (!state.isTerminal()) {
        state.play(state.getRandomMove(
            random == null ? ThreadLocalRandom.current() : random));
      }

      s += state.getResult().asDouble();
    }

    return s;
  }

  @Override
  public RandomSampleEvaluator<G> clone() {
    return new RandomSampleEvaluator<G>(nsamples, random);
  }
}
