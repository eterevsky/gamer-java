package players;

import gamer.Game;
import gamer.GameState;
import gamer.Move;
import gamer.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;

public class MonteCarloUcb<G extends Game> implements Player<G> {
  private final int SAMPLES_BATCH = 8;

  private double timeoutInSec = 1;
  private EvaluationQueue<G, ShallowNode<G>> evaluationQueue =
      new EvaluationQueue<>(new RandomSampleEvaluator<G>(SAMPLES_BATCH));

  public MonteCarloUcb() {}

  public MonteCarloUcb setTimeout(double timeoutInSec) {
    this.timeoutInSec = timeoutInSec;
    return this;
  }

  public MonteCarloUcb setExecutor(ExecutorService executor, int maxWorkers) {
    evaluationQueue = new EvaluationQueue<>(
        new RandomSampleEvaluator<G>(SAMPLES_BATCH), executor, maxWorkers);
    return this;
  }

  public Move<G> selectMove(GameState<G> state) throws Exception {
    long startTime = System.currentTimeMillis();

    List<ShallowNode<G>> nodes = new ArrayList<>();
    PriorityQueue<QueueElement<ShallowNode<G>>> queue = new PriorityQueue<>();
    for (Move<G> move : state.getAvailableMoves()) {
      ShallowNode<G> node = new ShallowNode<>(state, move);
      nodes.add(node);
      queue.add(new QueueElement<>(node, -1));
    }

    boolean player = state.getPlayer();
    int totalSamples = 0;
    while (System.currentTimeMillis() - startTime < timeoutInSec * 1000) {
      while (evaluationQueue.needMoreWork()) {
        ShallowNode<G> node = queue.poll().item;
        evaluationQueue.put(node, node.state);
      }

      EvaluationQueue<G, ShallowNode<G>>.LabeledResult result =
          evaluationQueue.get();
      ShallowNode<G> node = result.label;
      node.addSamples(result.result, SAMPLES_BATCH);
      totalSamples += SAMPLES_BATCH;
      double newPriority =
          node.getValue(player) / Math.sqrt(Math.log(totalSamples)) +
          Math.sqrt(2.0 * SAMPLES_BATCH / node.getSamplesWithProcessed());
      queue.add(new QueueElement<>(node, -newPriority));
    }

    ShallowNode<G> bestNode = null;

    totalSamples = 0;
    for (QueueElement<ShallowNode<G>> queueElement : queue) {
      ShallowNode<G> node = queueElement.item;
      totalSamples += node.samples;
      if (bestNode == null ||
          node.getValue(player) > bestNode.getValue(player)) {
        bestNode = node;
      }
    }

    System.out.format(
        "%f over %d (%d)\n", bestNode.getValue(player), bestNode.samples,
        totalSamples);
    return bestNode.move;
  }
}
