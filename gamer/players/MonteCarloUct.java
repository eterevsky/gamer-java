package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MonteCarloUct<G extends Game> implements Player<G> {
  private long samplesLimit = -1;
  private int samplesBatch = 16;
  private double timeoutInSec = 1;
  private ExecutorService executorService;
  private int maxWorkers = 1;

  private EvaluationQueue<G, Node<G>> evaluationQueue = null;

  private static class Node<G extends Game> {
    private int samples = 0;
    private int pendingSamples = 0;
    private double sumValue = 0.0;
    private final GameState<G> state;
    private final Node<G> parent;  // May be null.
    final Move<G> lastMove;  // May be null.
    private List<Node<G>> children = null;

    Node(Node<G> parent, GameState<G> state, Move<G> lastMove) {
      this.parent = parent;
      this.state = state.clone();  // TODO: make state immutable
      this.lastMove = lastMove;
    }

    boolean isUnexplored() {
      return samples + pendingSamples <= 1;
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

        double priority = child.getPriority(totalSamplesLog);
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

    double getValue() {
      return state.getPlayer() ? (sumValue / samples)
                               : (1 - sumValue / samples);
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

    private void initChildren() {
      List<Move<G>> moves = state.getMoves();
      children = new ArrayList<>(moves.size());
      for (Move<G> move : moves) {
        GameState<G> newState = state.clone();
        newState.play(move);
        children.add(new Node<>(this, newState, move));
      }
    }

    private double getPriority(double parentSamplesLog) {
      int totalSamples = samples + pendingSamples;
      assert totalSamples > 0;

      return (state.getPlayer() ? sumValue / totalSamples
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

  private void initEvaluationQueue() {
    if (evaluationQueue != null)
      return;

    Evaluator<G> evaluator = new RandomSampleEvaluator<G>(samplesBatch);

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
    for (Node<G> node : root.getChildren()) {
      if (bestNode == null || node.getValue() > bestNode.getValue())
        bestNode = node;
    }

    System.out.println(root.toString());

    return bestNode.lastMove;
  }
}
