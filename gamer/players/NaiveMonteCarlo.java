package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class NaiveMonteCarlo<G extends Game> implements Player<G> {
  private final int SAMPLES_BATCH = 8;

  private double timeoutInSec = 1;
  private EvaluationQueue<G, ShallowNode<G>> evaluationQueue =
      new EvaluationQueue<>(new RandomSampleEvaluator<G>(SAMPLES_BATCH));

  public NaiveMonteCarlo() {}

  public NaiveMonteCarlo setTimeout(double timeoutInSec) {
    this.timeoutInSec = timeoutInSec;
    return this;
  }

  public NaiveMonteCarlo setExecutor(ExecutorService executor, int maxWorkers) {
    evaluationQueue = new EvaluationQueue<>(
        new RandomSampleEvaluator<G>(SAMPLES_BATCH), executor, maxWorkers);
    return this;
  }

  public Move<G> selectMove(GameState<G> state) throws Exception {
    long startTime = System.currentTimeMillis();

    List<ShallowNode<G>> nodes = new ArrayList<>();
    for (Move<G> move : state.getAvailableMoves()) {
      nodes.add(new ShallowNode<>(state, move));
    }

    int imove = 0;

    while (System.currentTimeMillis() - startTime < timeoutInSec * 1000) {
      while (evaluationQueue.needMoreWork()) {
        evaluationQueue.put(nodes.get(imove), nodes.get(imove).state);
        imove = (imove + 1) % nodes.size();
      }

      EvaluationQueue<G, ShallowNode<G>>.LabeledResult result =
          evaluationQueue.get();
      result.label.addSamples(result.result, SAMPLES_BATCH);
    }

    boolean player = state.getPlayer();
    ShallowNode<G> bestNode = null;
    int totalSamples = 0;
    for (ShallowNode<G> node : nodes) {
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