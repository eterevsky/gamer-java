package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;

class RandomSampleEvaluator<G extends Game> implements Evaluator<G> {
  private final int nsamples;

  RandomSampleEvaluator(int nsamples) {
    this.nsamples = nsamples;
  }

  public double evaluate(GameState<G> origState) {
    int s = 0;

    for (int i = 0; i < nsamples; i++) {
      GameState<G> state = origState.clone();
      while (!state.isTerminal()) {
        state.play(state.getRandomMove());
      }

      s += state.getResult().asDouble();
    }

    return s;
  }

  @Override
  public RandomSampleEvaluator<G> clone() {
    return new RandomSampleEvaluator<G>(nsamples);
  }
}
