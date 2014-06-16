package players;

import gamer.Game;
import gamer.GameState;
import gamer.Move;
import gamer.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class NaiveMonteCarlo implements Player {
  private double timeoutInSec;
  private ExecutorService executor = null;
  private int maxWorkers;

  private class Variant<G extends Game> {
    final Move<G> move;
    final GameState<G> state;
    int nsamples = 0;
    int wins = 0;

    Variant(GameState<G> parent, Move<G> move) {
      this.move = move;
      state = parent.clone();
      state.play(move);
    }

    void addSample(int result) {
      wins += result;
      nsamples += 1;
    }

    double result() {
      return (double) wins / nsamples;
    }
  }

  public NaiveMonteCarlo() {}

  public NaiveMonteCarlo setTimeout(double timeoutInSec) {
    this.timeoutInSec = timeoutInSec;
    return this;
  }

  public NaiveMonteCarlo setExecutor(ExecutorService executor, int maxWorkers) {
    this.executor = executor;
    this.maxWorkers = maxWorkers;
    return this;
  }

  public <G extends Game> Move<G> selectMove(GameState<G> state)
      throws Exception {
    long startTime = System.currentTimeMillis();

    List<Variant<G>> variants = new ArrayList<>();
    for (Move<G> move : state.getAvailableMoves()) {
      variants.add(new Variant<>(state, move));
    }

    EvaluationQueue<G, Integer> evaluationQueue = new EvaluationQueue<>(
        new RandomSampleEvaluator<G>(), executor, maxWorkers);

    int imove = 0;

    try {
      while (System.currentTimeMillis() - startTime < timeoutInSec * 1000) {
        while (evaluationQueue.needMoreWork()) {
          evaluationQueue.put(imove, variants.get(imove).state);
          imove = (imove + 1) % variants.size();
        }

        EvaluationQueue<G, Integer>.LabeledResult result =
            evaluationQueue.get();
        variants.get(result.label).addSample(result.result);
      }
    } finally {
      evaluationQueue.shutdown();
    }

    boolean iAmFirst = state.getPlayer();
    Variant<G> bestVariant = null;
    for (Variant<G> variant : variants) {
      if (bestVariant == null ||
          iAmFirst && variant.result() > bestVariant.result() ||
          !iAmFirst && variant.result() < bestVariant.result()) {
        bestVariant = variant;
      }
    }

    System.out.format(
        "%f over %d\n", bestVariant.result(), bestVariant.nsamples);

    return bestVariant.move;
  }
}
