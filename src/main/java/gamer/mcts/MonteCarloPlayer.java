package gamer.mcts;

import gamer.def.ComputerPlayer;
import gamer.def.Game;
import gamer.def.Move;
import gamer.def.State;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MonteCarloPlayer<S extends State<S, M>, M extends Move>
    implements ComputerPlayer<S, M> {
  private Game<S, M> game;
  private long timeout = 1000;
  private long samplesLimit = 0;
  private int samplesBatch = 1;
  private int workers = 1;
  private int childrenThreshold = 1;
  private String report;

  public MonteCarloPlayer(Game<S, M> game) {
    this.game = game;
  }

  @Override
  public String getName() {
    String batchStr = this.samplesBatch < 2 ? "" : String.format(" batch=%d",
                                                                 this.samplesBatch);

    String workersStr = this.workers < 2 ? "" : String.format(" threads=%d",
                                                              this.samplesBatch);

    String childrenThresholdStr =
        this.childrenThreshold <= this.samplesBatch
            ? ""
            : String.format(" childrenThreshold=%d", this.childrenThreshold);

    return String.format("MonteCarloPlayer(timeout=%0.1s%s%s)", timeout /
        1000.0, batchStr, workersStr);
  }

  @Override
  public void setMaxWorkers(int maxWorkers) {
    this.workers = maxWorkers;
  }

  @Override
  public void setMaxSamples(long maxSamples) {
    this.samplesLimit = maxSamples;
  }

  @Override
  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  public void setChildrenThreshold(int threshold) {
    if (threshold < 1) {
      throw new RuntimeException("Children threshold should be at least 1");
    }
    childrenThreshold = threshold;
  }

  @Override
  public M selectMove(S state) {
    assert state.getPlayer() >= 0;
    assert state.getPlayer() < 2;  // Can't yet handle games with > 2 players.

    Node<S, M> root = new Node<>(null, state, null);
    long deadline = timeout > 0 ? System.currentTimeMillis() + timeout : -1;

    if (workers > 0) {
      ExecutorService executor = Executors.newFixedThreadPool(workers);
      List<Future<?>> tasks = new ArrayList<>();
      for (int i = 0; i < workers; i++) {
        tasks.add(executor.submit(() -> worker(root, state, deadline)));
      }

      for (Future<?> task : tasks) {
        try {
          task.get();
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      }
      executor.shutdown();
    } else {
      worker(root, state, deadline);
    }

    assert root.hasChildren();
    Node<S, M> bestChild = null;
    double bestValue = state.getPlayer() == 0 ? game.getMinPayoff()
                                              : game.getMaxPayoff();

    for (Node<S, M> node : root.getChildren()) {
      if ((state.getPlayer() == 0 ? (node.getPayoff() > bestValue)
                                  : (node.getPayoff() < bestValue))) {
        bestChild = node;
        bestValue = node.getPayoff();
      }
    }

    report = String.format(
        "Move: %s%n%s%n",
        state.moveToString(bestChild.getMove()),
        root.toStringNested(state, 16));

    return bestChild.getMove();
  }

  @Override
  public String getReport() {
    return report;
  }

  private void worker(Node<S, M> root, S rootState, long deadline) {
    while (!root.hasExactPayoff() &&
        (samplesLimit<= 0 || root.getTotalSamples() < samplesLimit) &&
        (deadline <= 0 || System.currentTimeMillis() < deadline)) {
      S state = rootState.clone();
      Node<S, M> node = root;
      int depth;
      Node<S, M> next = node.selectChildOrAddPending(state, samplesBatch);
      while (next != Node.NO_CHILDREN && next != Node.KNOW_EXACT) {
        node = next;
        state.play(node.getMove());
        next = node.selectChildOrAddPending(state, samplesBatch);
      }

      if (next == Node.KNOW_EXACT) {
        continue;
      }

      double value = 0;
      for (int i = 0; i < samplesBatch; i++) {
        S position;
        if (i < samplesBatch - 1) {
          position = state.clone();
        } else {
          position = state;
        }
        int moves = 0;
        do {
          position.play(selector.select(position));
          // sResult = (solver != null) ? solver.solve(position) : null;
          moves += 1;
        } while (!position.isTerminal() && sResult == null);

        if (position.isTerminal()) {
          value += Math.pow(PAYOFF_SCALE_FACTOR, moves) * position.getPayoff(0);
        } else {
          value += Math.pow(PAYOFF_SCALE_FACTOR, moves) * sResult.payoff;
        }
      }
      node.addSamples(samplesBatch, value / samplesBatch);
    }

  }
}
