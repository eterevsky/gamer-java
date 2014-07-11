package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class MonteCarloUcb<G extends Game> implements Player<G> {
  private long samplesLimit = -1;
  private int samplesBatch = 16;
  private double timeoutInSec = 1;
  private ExecutorService executorService;
  private int maxWorkers = 1;

  private final Logger LOG = Logger.getLogger("gamer.players.MonteCarloUcb");

  private EvaluationQueue<G, ShallowNode<G>> evaluationQueue = null;

  public MonteCarloUcb() {}

  public MonteCarloUcb<G> setTimeout(double timeoutInSec) {
    this.timeoutInSec = timeoutInSec;
    return this;
  }

  public MonteCarloUcb<G> setSamplesLimit(long samplesLimit) {
    this.samplesLimit = samplesLimit;
    return this;
  }

  MonteCarloUcb<G> setSamplesBatch(int samplesBatch) {
    this.samplesBatch = samplesBatch;
    evaluationQueue = null;
    return this;
  }

  public MonteCarloUcb<G> setExecutor(
      ExecutorService executor, int maxWorkers) {
    executorService = executor;
    this.maxWorkers = maxWorkers;
    evaluationQueue = null;
    return this;
  }

  private void initEvaluationQueue() {
    if (evaluationQueue != null)
      return;

    if (executorService != null) {
      evaluationQueue = new EvaluationQueue<>(
          new RandomSampleEvaluator<G>(samplesBatch), executorService,
          maxWorkers);
    } else {
      evaluationQueue =
          new EvaluationQueue<>(new RandomSampleEvaluator<G>(samplesBatch));
    }
  }

  public Move<G> selectMove(GameState<G> state) {
    initEvaluationQueue();

    long startTime = System.currentTimeMillis();

    List<ShallowNode<G>> nodes = new ArrayList<>();
    for (Move<G> move : state.getMoves()) {
      ShallowNode<G> node = new ShallowNode<>(state, move);
      nodes.add(node);
    }

    boolean player = state.getPlayer();
    int totalSamples = 0;
    while ((samplesLimit < 0 || totalSamples < samplesLimit) &&
           (timeoutInSec < 0 ||
            System.currentTimeMillis() - startTime < timeoutInSec * 1000)) {
      while (evaluationQueue.needMoreWork()) {
        ShallowNode<G> bestNode = null;
        double bestNodePriority = 0.0;
        for (ShallowNode<G> node : nodes) {
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

        evaluationQueue.put(bestNode, bestNode.state);
      }

      EvaluationQueue<G, ShallowNode<G>>.LabeledResult result =
          evaluationQueue.get();
      ShallowNode<G> node = result.label;
      node.addSamples(result.result, samplesBatch);
      totalSamples += samplesBatch;
    }

    ShallowNode<G> bestNode = null;
    for (ShallowNode<G> node : nodes) {
      if (bestNode == null ||
          node.getValue(player) > bestNode.getValue(player)) {
        bestNode = node;
      }
    }

    LOG.info(String.format(
        "%f over %d (%d)\n", bestNode.getValue(player), bestNode.samples,
        totalSamples));
    return bestNode.move;
  }
}
