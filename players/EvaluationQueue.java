package players;

import gamer.Game;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class EvaluationQueue<G extends Game, L> {
  private Executor executor;
  private int nworkers;
  private Evaluator<G> evaluator;
  private BlockingQueue<LabeledState> states;
  private BlockingQueue<LabeledResult> results;
  boolean moreWork = true;

  private class LabeledState {
    L label;
    GameState<G> state;

    LabeledState(L label, GameState<G> state) {
      this.label = label;
      this.state = state;
    }
  }

  public class LabeledResult {
    L label;
    int result;

    LabeledState(L label, int result) {
      this.label = label;
      this.result = result;
    }
  }

  private class Worker extends Runnable {
    private final Evaluator<G> evaluator;
    private final BlockingQueue<LabeledState> states;
    private final BlockingQueue<LabeledResult> results;

    Worker(Evaluator<G> evaluator,
           BlockingQueue<LabeledState> states,
           BlockingQueue<LabeledResult> results) {
      this.evaluator = evaluator;
      this.states = states;
      this.results = results;
    }

    void run() {
      while (true) {
        LabeledState lstate = states.take();
        int result = evaluator.evaluate(lstate.state);
        results.put(new LabeledResult(lstate.label, result));
      }
    }
  }

  EvaluationQueue(Evaluator<G> evaluator,
                  ExecutorService executor,
                  int nworkers) {
    if (executor == null) {
      nworkers = 0;
    }

    this.executor = executor;
    this.nworkers = nworkers;
    this.evaluator = evaluator;

    states = new BlockingQueue<>();
    results = new BlockingQueue<>();

    for (int i = 0; i < nworkers; i++) {
      executor.execute(new Worker(evaluator.clone(), states, results));
    }
  }

  boolean needMoreWork() {
    if (states.peek() == null) {
      moreWork = true;
      return true;
    }

    if (moreWork) {
      moreWork = false;
      return true;
    }

    return false;
  }

  void put(L label, GameState<G> state) throws RuntimeException {
    if (!states.offer(LabeledState(label, state))) {
      throw RuntimeException("State evaluation queue overflow.");
    }
  }

  // Blocking.
  LabeledResult get() {
    if (executor != null) {
      return this.results.take();
    } else {
      LabeledState lstate = states.take();
      int result = evaluator.evaluate(lstate.state);
      return new LabeledResult(lstate.label, result);
    }
  }
}
