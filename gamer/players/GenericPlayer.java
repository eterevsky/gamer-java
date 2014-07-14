package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;  // TODO: remove
import java.util.logging.Logger;

abstract class GenericPlayer<G extends Game> implements Player<G> {
  private long samplesLimit = -1;
  private int samplesBatch = 1;
  private long timeout = 1000;
  private Executor executor = null;
  private int workers = 1;
  private Random random = null;
  private final Logger LOG = Logger.getLogger("gamer.players.GenericPlayer");

  @Override
  public GenericPlayer<G> setTimeout(long timeout) {
    this.timeout = timeout;
    return this;
  }

  @Override
  public GenericPlayer<G> setSamplesLimit(long samplesLimit) {
    this.samplesLimit = samplesLimit;
    return this;
  }

  GenericPlayer<G> setSamplesBatch(int samplesBatch) {
    this.samplesBatch = samplesBatch;
    return this;
  }

  @Override
  public GenericPlayer<G> setExecutor(ExecutorService executor, int workers) {
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

  @Override
  public Move<G> selectMove(GameState<G> state) {
    Node<G> root = getRoot(state);

    long finishTime = timeout > 0 ? System.currentTimeMillis() + timeout : -1;
    Sampler<G> sampler = new Sampler<G>(
        root, finishTime, samplesLimit, samplesBatch, random);

    sampler.run();

    Node<G> bestNode = null;
    double bestValue = 2.0;
    boolean player = state.status().getPlayer();
    for (Node<G> node : root.getChildren()) {
      if (node.getValue() < bestValue) {
        bestNode = node;
        bestValue = node.getValue();
      }
    }

    LOG.info(String.format("%s: %f over %d (%d)\n",
        bestNode.getMove(), 1 - bestNode.getValue(), bestNode.getSamples(),
        root.getSamples()));

    return bestNode.getMove();
  }
}
