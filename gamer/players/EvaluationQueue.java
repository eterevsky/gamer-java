package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

class EvaluationQueue<G extends Game, L> {
  private ExecutorService executor;
  private int nworkers;
  private Evaluator<G> evaluator;
  private List<Future<?>> tasks = new ArrayList<>();
  private BlockingQueue<LabeledState> states = new LinkedBlockingQueue<>();
  private BlockingQueue<LabeledResult> results = new LinkedBlockingQueue<>();
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
    double result;

    LabeledResult(L label, double result) {
      this.label = label;
      this.result = result;
    }
  }

  private class Worker implements Runnable {
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

    public void run() {
      try {
        while (true) {
          LabeledState lstate = states.take();
          double result = evaluator.evaluate(lstate.state);
          results.put(new LabeledResult(lstate.label, result));
        }
      } catch (InterruptedException e) {}
    }
  }

  EvaluationQueue(Evaluator<G> evaluator) {
    this(evaluator, null, 0);
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

    for (int i = 0; i < nworkers; i++) {
      tasks.add(
          executor.submit(new Worker(evaluator.clone(), states, results)));
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
    if (!states.offer(new LabeledState(label, state))) {
      throw new RuntimeException("State evaluation queue overflow.");
    }
  }

  // Blocking.
  LabeledResult get() {
    try {
      if (executor != null) {
        return this.results.take();
      } else {
        LabeledState lstate = states.take();
        double result = evaluator.evaluate(lstate.state);
        return new LabeledResult(lstate.label, result);
      }
    } catch (InterruptedException e) {
      return null;
    }
  }

  void shutdown() {
    for (Future<?> task : tasks) {
      task.cancel(true);
    }
  }
}
