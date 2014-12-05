package gamer.players;

import gamer.def.Move;
import gamer.def.ComputerPlayer;
import gamer.def.Position;
import gamer.def.Solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Logger;

abstract class GenericPlayer<P extends Position<P, M>, M extends Move>
    implements ComputerPlayer<P, M> {
  private long samplesLimit = -1;
  private long timeout = 1000;
  private ExecutorService executor = null;
  private int workers = 1;
  private Random random = null;
  private Solver<P> solver = null;
  private String report;

  protected int samplesBatch = 1;
  protected String name = null;
  protected NodeContext<P> nodeContext = new NodeContext<P>();

  @Override
  public boolean isHuman() {
    return false;
  }

  @Override
  public final void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  /*
   * @see gamer.def.ComputerPlayer#setMaxSamples(long)
   */
  @Override
  public void setMaxSamples(long maxSamples) {
    this.samplesLimit = samplesLimit;    
  }

  public final void setSamplesBatch(int samplesBatch) {
    this.samplesBatch = samplesBatch;
  }

  @Override
  public final void setMaxWorkers(int workers) {
    this.workers = workers;
    if (workers > 1) {
      executor = Executors.newFixedThreadPool(workers);
    } else {
      executor = null;
    }
  }

  @Override
  public void setRandom(Random random) {
    this.random = random;
  }

  public void setFindExact(boolean exact) {
    this.nodeContext = new NodeContext<P>(exact, solver);
  }

  public void addSolver(Solver<P> solver) {
    this.solver = solver;
    this.nodeContext =
        new NodeContext<P>(this.nodeContext.propagateExact, solver);
  }

  @Override
  public String getName() {
    if (name != null)
      return name;
    String threads =
        this.executor == null ? "t0" : String.format("t%d", this.workers);
    String s = String.format(
        "%s b%d %s %.1fs", getClass().getSimpleName(), this.samplesBatch, threads,
        timeout/1000.0);
    if (this.nodeContext.propagateExact)
      s += " +exact";

    if (this.solver != null)
      s += " " + this.solver.getClass().getSimpleName();

    return s;
  }

  @Override
  public String getReport() {
    return report;
  }

  abstract protected Node<P, M> getRoot(P state);

  protected Sampler<P, M> newSampler(
      Node<P, M> root, long finishTime, long samplesLimit, int samplesBatch,
      Random random) {
    Sampler<P, M> sampler = new Sampler<>(
        root, finishTime, samplesLimit, samplesBatch, random);
    if (solver != null)
      sampler.setSolver(solver);
    return sampler;
  }

  protected long getCurrentTime() {
    return System.currentTimeMillis();
  }

  private M selectMoveUsingSolver(P state, Solver.Result result) {
    GameStatus status = result.status;
    double value = result.value(position.getPlayer());
    boolean winning = value > 0;
    boolean loosing = value < 0;

    for (M move : state.getMoves()) {
      P next = state.play(move);
      Solver.Result nextResult = solver.evaluate(next);
      if (nextResult == null || nextResult.status != status) {
        assert winning;
        continue;
      }

      if (winning && nextResult.moves < result.moves ||
          loosing && nextResult.moves >= result.moves - 1 ||
          !winning && !loosing) {
        return move;
      }
    }

    throw new RuntimeException("Internal error");
  }

  @Override
  public M selectMove(P state) {
    if (solver != null) {
      Solver.Result result = solver.evaluate(state);
      if (result != null)
        return selectMoveUsingSolver(state, result);
    }

    Node<P, M> root = getRoot(state);

    long finishTime = timeout > 0 ? getCurrentTime() + timeout : -1;

    if (workers > 0) {
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
    Node<P, M> bestNode = null;
    double bestValue = player ? -1 : 2;
    for (Node<P, M> node : root.getChildren()) {
      if (player ? (node.getValue() > bestValue)
                 : (node.getValue() < bestValue)) {
        bestNode = node;
        bestValue = node.getValue();
      }
    }

    report = String.format(
        "%s : %f over %d (%d)",
        state.moveToString(bestNode.getMove()),
        bestNode.getValue(), bestNode.getSamples(), root.getSamples());

    return bestNode.getMove();
  }
}
