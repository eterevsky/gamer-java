package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;

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
    private int wins = 0;
    private final GameState<G> state;
    private final Node<G> parent;  // May be null.
    private final Move<G> lastMove;  // May be null.
    private List<Node<G>> children = null;

    Node(Node<G> parent, GameState<G> state, Move<G> lastMove) {
      this.parent = parent;
      this.state = state.clone();  // TODO: make state immutable
      this.lastMove = lastMove;
    }

    boolean isUnexplored() {
      return samples == 0;
    }

    private void initChildren() {
      List<Move<G>> moves = state
      children = new ArrayList<>(moves.size());
      for (Move<G> move : moves) {
        GameState<G> newState = state.clone();
        children.add(new Node(this, newState, move));
      }
    }

    Node<G> select() {
      if (children == null)
        initChildren();

      double bestNodePriority = 0.0;
      Move<G> bestMove = null;
      for (Map.Entry<Move<G>, Node<G>> entry : children) {
        Move<G> move = entry.getKey();
        Node<G> node = node.getValue();
        if (node == null) {
          node = new Node(this,
        }

        double priority;
        if (node.getSamplesWithProcessed() == 0) {
          priority = Double.MAX_VALUE;
        } else {
          priority = Math.sqrt(1.0 / node.getSamplesWithProcessed());
          if (totalSamples > 1.0) {
            priority += node.getValue(player) /
                        Math.sqrt(Math.log(totalSamples));
          }
        }

        if (bestNode == null || priority > bestNodePriority) {
          bestNode = node;
          bestNodePriority = priority;
        }
      }

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

    PositionNode<G> root =
        new PositionNode<>(null, state.getPlayer(), state.getMoves());

/*    while (System.currentTimeMillis() - startTime < timeout) {
      if (runningSamplers < MAX_SAMPLERS) {
        NodeAndState<G> = getNodeFromQueue(root, state);

      }

...

    }

    executor.shutdownNow();

...

    System.out.format("%d of %d\n",
                      winsByMove[bestMove],
                      samplesByMove[bestMove]);

    return bestMove;
*/
    return null;
  }
}
