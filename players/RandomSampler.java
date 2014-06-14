package players;

import gamer.Game;
import gamer.GameState;
import gamer.Move;
import java.util.concurrent.Callable;

/* package */ class RandomSampler<T> implements Callable<Sample<T>> {
  private T label;
  private GameState<?> state;
  private int nsamples;

  RandomSampler(T label, GameState<?> state, int nsamples) {
    this.label = label;
    this.state = state;
    this.nsamples = nsamples;
  }

  @SuppressWarnings("unchecked")
  public Sample<T> call() {
    RandomPlayer randomPlayer = new RandomPlayer();

    int result = 0;

    for (int i = 0; i < nsamples; i++) {
      GameState stateCopy = state.clone();
      while (!stateCopy.isTerminal()) {
        stateCopy.play(randomPlayer.selectMove(stateCopy));
      }
      result += stateCopy.getResult();
    }

    return new Sample<T>(label, nsamples, result);
  }
}
