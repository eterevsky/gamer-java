package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Logger;

abstract class GenericPlayer<G extends Game> implements Player<G> {
  private long samplesLimit = -1;
  private int samplesBatch = 16;
  private long timeout = 1000;
  private ExecutorService executor = null;
  private int workers = 1;
  private Random random = null;
  private final Logger LOG = Logger.getLogger("gamer.players.GenericPlayer");

  @Override
  public final GenericPlayer<G> setTimeout(long timeout) {
    this.timeout = timeout;
    return this;
  }

  @Override
  public final GenericPlayer<G> setSamplesLimit(long samplesLimit) {
    this.samplesLimit = samplesLimit;
    return this;
  }

  public final GenericPlayer<G> setSamplesBatch(int samplesBatch) {
    this.samplesBatch = samplesBatch;
    return this;
  }

  @Override
  public final GenericPlayer<G> setExecutor(ExecutorService executor,
                                            int workers) {
    this.executor = executor;
    this.workers = workers;
    return this;
  }

  @Override
  public GenericPlayer<G> setRandom(Random random) {
    this.random = random;
    return this;
  }

  abstract protected Node<G> getRoot(GameState<G> state);

  protected Sampler<G> newSampler(
      Node<G> root, long finishTime, long samplesLimit, int samplesBatch,
      Random random) {
    return new Sampler<G>(root, finishTime, samplesLimit, samplesBatch, random);
  }

  protected long getCurrentTime() {
    return System.currentTimeMillis();
  }

  @Override
  public Move<G> selectMove(GameState<G> state) {
    Node<G> root = getRoot(state);

    long finishTime = timeout > 0 ? getCurrentTime() + timeout : -1;

    if (executor != null) {
      List<Future<?>> tasks = new ArrayList<>();
      for (int i = 0; i < workers; i++) {
        tasks.add(executor.submit(
            newSampler(root, finishTime, samplesLimit, samplesBatch, random)));
      }

      for (Future<?> task : tasks) {
        try {
          task.get();
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      }
    } else {
      newSampler(root, finishTime, samplesLimit, samplesBatch, random).run();
    }


    boolean player = state.status().getPlayer();
    Node<G> bestNode = null;
    double bestValue = player ? -1 : 2;
    for (Node<G> node : root.getChildren()) {
      if (player ? (node.getValue() > bestValue)
                 : (node.getValue() < bestValue)) {
        bestNode = node;
        bestValue = node.getValue();
      }
    }

    LOG.info(String.format("%s: %f over %d (%d)\n",
        bestNode.getMove(), bestNode.getValue(), bestNode.getSamples(),
        root.getSamples()));

    return bestNode.getMove();
  }
}
