package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class MonteCarloUct<G extends Game> implements Player<G> {
  private long samplesLimit = -1;
  private int samplesBatch = 1;
  private double timeoutInSec = 1;
  private ExecutorService executorService;
  private int maxWorkers = 1;
  private Random random = null;

  private EvaluationQueue<G, Node<G>> evaluationQueue = null;
  private final Logger LOG = Logger.getLogger("gamer.players.MonteCarloUct");

  private static class Node<G extends Game> {
    private int samples = 0;
    private int pendingSamples = 0;
    private double sumValue = 0.0;
    private final GameState<G> state;
    private final Node<G> parent;  // null for root
    final Move<G> lastMove;  // null for root
    private List<Node<G>> children = null;

    Node(Node<G> parent, GameState<G> state, Move<G> lastMove) {
      this.parent = parent;
      this.state = state.clone();  // TODO: make state immutable
      this.lastMove = lastMove;
    }

    boolean isUnexplored() {
      return samples + pendingSamples <= 2;
    }

    Node<G> select() {
      if (children == null)
        initChildren();

      double totalSamplesLog = 2 * Math.log(samples + pendingSamples);
      assert totalSamplesLog >= 0;

      Node<G> bestChild = null;
      double bestChildPrio = 0;
      for (Node<G> child : children) {
        if (child.isUnexplored())
          return child;

        double priority = child.getPriority(totalSamplesLog,
                                            state.status().getPlayer());
        if (bestChild == null || priority > bestChildPrio) {
          bestChild = child;
          bestChildPrio = priority;
        }
      }

      return bestChild;
    }

    void addPendingSamples(int ps) {
      pendingSamples += ps;
      if (parent != null)
        parent.addPendingSamples(ps);
    }

    void addSamples(int s, double v) {
      samples += s;
      pendingSamples -= s;
      assert pendingSamples >= 0;

      sumValue += v;
      if (parent != null)
        parent.addSamples(s, v);
    }

    double getValue(boolean player) {
      return player ? (sumValue / samples) : (1 - sumValue / samples);
    }

    int getSamples() {
      return samples;
    }

    List<Node<G>> getChildren() {
      return children;
    }

    GameState<G> getState() {
      return state;
    }

    public String toString() {
      return toString(0);
    }

    public String toString(int indent) {
      StringBuilder builder = new StringBuilder();
      builder.append('\n');
      for (int i = 0; i < indent; i++) {
        builder.append(' ');
      }
      if (lastMove != null) {
        builder.append(lastMove.toString());
      } else {
        builder.append(state.toString());
      }
      builder.append(
          String.format(" %.1f/%d/%d", sumValue, samples, pendingSamples));
      if (children != null) {
        for (Node<G> child : children) {
          builder.append(child.toString(indent + 2));
        }
      }

      return builder.toString();
    }

    private void initChildren() {
      List<Move<G>> moves = state.getMoves();
      children = new ArrayList<>(moves.size());
      for (Move<G> move : moves) {
        GameState<G> newState = state.clone();
        newState.play(move);
        children.add(new Node<>(this, newState, move));
      }
    }

    private double getPriority(double parentSamplesLog, boolean lastPlayer) {
      int totalSamples = samples + pendingSamples;
      assert totalSamples > 0;

      return (lastPlayer ? sumValue / totalSamples
                         : (samples - sumValue) / totalSamples) +
             Math.sqrt(parentSamplesLog / totalSamples);
    }
  }

  public MonteCarloUct() {}

  public MonteCarloUct<G> setTimeout(double timeoutInSec) {
    this.timeoutInSec = timeoutInSec;
    return this;
  }

  public MonteCarloUct<G> setSamplesLimit(long samplesLimit) {
    this.samplesLimit = samplesLimit;
    return this;
  }

  MonteCarloUct<G> setSamplesBatch(int samplesBatch) {
    this.samplesBatch = samplesBatch;
    evaluationQueue = null;
    return this;
  }

  public MonteCarloUct<G> setExecutor(
      ExecutorService executor, int maxWorkers) {
    executorService = executor;
    this.maxWorkers = maxWorkers;
    evaluationQueue = null;
    return this;
  }

  public MonteCarloUct<G> setRandom(Random random) {
    this.random = random;
    return this;
  }

  private void initEvaluationQueue() {
    if (evaluationQueue != null)
      return;

    Evaluator<G> evaluator = new RandomSampleEvaluator<G>(samplesBatch, random);

    if (executorService != null) {
      evaluationQueue =
          new EvaluationQueue<>(evaluator, executorService, maxWorkers);
    } else {
      evaluationQueue = new EvaluationQueue<>(evaluator);
    }
  }

  public Move<G> selectMove(GameState<G> state) {
    long startTime = System.currentTimeMillis();
    initEvaluationQueue();

    Node<G> root = new Node<>(null, state, null);

    while ((samplesLimit < 0 || root.getSamples() < samplesLimit) &&
           (timeoutInSec < 0 ||
            System.currentTimeMillis() - startTime < timeoutInSec * 1000)) {
      while (evaluationQueue.needMoreWork()) {
        Node<G> node = root;
        while (!node.isUnexplored() && !node.getState().isTerminal()) {
          node = node.select();
        }
        evaluationQueue.put(node, node.getState());
        node.addPendingSamples(samplesBatch);
      }

      EvaluationQueue<G, Node<G>>.LabeledResult result =
          evaluationQueue.get();
      Node<G> node = result.label;
      node.addSamples(samplesBatch, result.result);
    }

    Node<G> bestNode = null;
    boolean player = state.status().getPlayer();
    for (Node<G> node : root.getChildren()) {
      if (bestNode == null || node.getSamples() > bestNode.getSamples())
        bestNode = node;
    }

    LOG.info(String.format(
        "%f over %d (%d)\n", bestNode.getValue(player), bestNode.getSamples(),
        root.getSamples()));

    return bestNode.lastMove;
  }
}
