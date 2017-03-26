package gamer.mcts;

import gamer.def.ComputerPlayer;
import gamer.def.Game;
import gamer.def.Move;
import gamer.def.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

public class MonteCarloPlayer<S extends State<S, M>, M extends Move>
    implements ComputerPlayer<S, M> {
  private long timeout = 1000;
  private long samplesLimit = 0;
  private int maxDepth = 0;
  private int samplesBatch = 1;
  private int workers = 1;
  private int childrenThreshold = 1;
  private String report;
  private final Node.Context<S, M> nodeContext;

  public MonteCarloPlayer(Game<S, M> game) {
    nodeContext = new Node.Context<>(game);
  }

  @Override
  public String getName() {
    String batchStr = this.samplesBatch < 2 ? "" : String
        .format(" batch=%d", this.samplesBatch);

    String workersStr =
        this.workers < 2 ? "" : String.format(" threads=%d", this.samplesBatch);

    String childrenThresholdStr =
        this.childrenThreshold <= this.samplesBatch ? "" : String
            .format(" childrenThreshold=%d", this.childrenThreshold);

    return String
        .format("MonteCarloPlayer(timeout=%0.1s%s%s)", timeout / 1000.0,
                batchStr, workersStr);
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

    Node<S, M> root = new Node<>(nodeContext, null, state, null);
    long deadline = timeout > 0 ? System.currentTimeMillis() + timeout : -1;

    if (workers > 1) {
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
    double bestValue =
        state.getPlayer() == 0 ? state.getGame().getMinPayoff() - 1
                               : state.getGame().getMaxPayoff() + 1;

    for (Node<S, M> node : root.getChildren()) {
      if ((state.getPlayer() == 0 ? (node.getPayoff() > bestValue)
                                  : (node.getPayoff() < bestValue))) {
        bestChild = node;
        bestValue = node.getPayoff();
      }
    }

    report = String
        .format("Move: %s%n%s%n", state.moveToString(bestChild.getMove()),
                root.toStringNested(state, 16));

    return bestChild.getMove();
  }

  @Override
  public String getReport() {
    return report;
  }

  private Node<S, M> selectChild(Node<S, M> node, S state) {
    assert node.hasChildren();

    if (state.isRandom()) {
      return node.getChild(state.getRandomMove());
    }

    if (node.getCompleteSamples() - samplesBatch <
        node.getChildren().size() * samplesBatch) {
      // Has children with 0 samples. Try 4 random children before iterating
      // through them.
      Random rng = ThreadLocalRandom.current();
      for (int i = 0; i < 4; i++) {
        Node<S, M> randomChild =
            node.getChildren().get(rng.nextInt(node.getChildren().size()));
        if (randomChild.getTotalSamples() == 0) {
          return randomChild;
        }
      }
      for (Node<S, M> child : node.getChildren()) {
        if (child.getTotalSamples() == 0) {
          return child;
        }
      }
    }

    assert node.getTotalSamples() - childrenThreshold > 1;
    double logTotalSamples =
        1.2 * Math.log(node.getTotalSamples() - childrenThreshold);
    double maxScore = state.getGame().getMinPayoff() - 1;
    Node<S, M> bestChild = null;

    for (Node<S, M> child : node.getChildren()) {
      double score =
          child.getBiasedScore(logTotalSamples, node.getPlayer() > 0);

      if (score > maxScore) {
        maxScore = score;
        bestChild = child;
      }
    }

    assert bestChild != null;
    return bestChild;
  }

  private static class TraversalResult<S extends State<S, M>, M extends Move> {
    Node<S, M> node;
    S state;
    int depth;

    TraversalResult(Node<S, M> node, S state, int depth) {
      this.node = node;
      this.state = state;
      this.depth = depth;
    }
  }

  private TraversalResult<S, M> traverse(Node<S, M> rootNode, S rootState) {
    Node<S, M> node = rootNode;
    S state = rootState.clone();
    int depth = 0;

    while (node.hasChildren() && !node.hasExactPayoff()) {
      node.addPendingSamples(samplesBatch);
      node = selectChild(node, state);
      state.play(node.getMove());
      depth += 1;
    }

    if (!state.isTerminal() && !node.hasExactPayoff() &&
        node.getTotalSamples() >= childrenThreshold &&
        (maxDepth <= 0 || depth < maxDepth)) {
      if (!node.hasChildren()) {
        node.initChildren(state);
      }
      node.addPendingSamples(samplesBatch);
      node = selectChild(node, state);
      state.play(node.getMove());
      depth += 1;
    }

    if (!node.hasExactPayoff()) {
      node.addPendingSamples(samplesBatch);
    }
    return new TraversalResult<S, M>(node, state, depth);
  }

  private M selectSamplingMove(S state) {
    return state.getRandomMove();
  }

  // TODO: add synchronization
  private void worker(Node<S, M> root, S rootState, long deadline) {
    while (!root.hasExactPayoff() &&
           (samplesLimit <= 0 || root.getTotalSamples() < samplesLimit) &&
           (deadline <= 0 || System.currentTimeMillis() < deadline)) {
      TraversalResult<S, M> result = traverse(root, rootState);
      Node<S, M> node = result.node;

      if (node.hasExactPayoff()) {
        int payoff = node.getExactPayoff();
        while (node != null) {
          if (node.hasExactPayoff()) {
            node.addExactSamples(samplesBatch);
          } else {
            node.addSamples(samplesBatch, payoff * samplesBatch,
                            payoff * payoff * samplesBatch);
          }
          node = node.getParent();
        }
        continue;
      }

      int value = 0;
      long valueSq = 0;
      for (int i = 0; i < samplesBatch; i++) {
        S state = i < samplesBatch - 1 ? result.state.clone() : result.state;
        do {
          state.play(selectSamplingMove(state));
        } while (!state.isTerminal());

        int payoff = state.getPayoff(0);
        value += payoff;
        valueSq += (long) payoff * payoff;
      }
      while (node != null) {
        node.addSamples(samplesBatch, value, valueSq);
        node = node.getParent();
      }
    }

  }
}
