package gamer.players;

import gamer.def.ComputerPlayer;
import gamer.def.Game;
import gamer.def.Move;
import gamer.def.MoveSelector;
import gamer.def.State;
import gamer.def.Solver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

abstract class GenericPlayer<S extends State<S, M>, M extends Move>
    implements ComputerPlayer<S, M> {
  protected int samplesBatch = 1;
  protected String name = null;
  protected NodeContext<S, M> nodeContext = null;
  private long samplesLimit = -1;
  private long timeout = 1000;
  private int workers = 0;
  private MoveSelector<S, M> selector = null;
  private Solver<S, M> solver = null;
  private String report;

  GenericPlayer(Game<S, M> game) {
    nodeContext = new NodeContext<>(true, null, game);
  }

  @Override
  public String getName() {
    if (name != null)
      return name;
    String threads = String.format("t%d", this.workers);
    String s = String.format(
        "%s b%d %s %.1fs", getClass().getSimpleName(), this.samplesBatch, threads,
        timeout / 1000.0);
    if (this.nodeContext.propagateExact)
      s += " +exact";

    s += " " + this.selector.getClass().getSimpleName();

    if (this.solver != null)
      s += " " + this.solver.getClass().getSimpleName();

    return s;
  }

  @Override
  public M selectMove(S state) {
    if (selector == null) {
      throw new IllegalStateException("Need to assign selector.");
    }

    if (solver != null) {
      Solver.Result<M> result = solver.solve(state);
      if (result != null)
        return result.move;
    }

    Node<S, M> root = getRoot(state);

    long finishTime = timeout > 0 ? getCurrentTime() + timeout : -1;

    if (workers > 0) {
      ExecutorService executor = Executors.newFixedThreadPool(workers);
      List<Future<?>> tasks = new ArrayList<>();
      for (int i = 0; i < workers; i++) {
        tasks.add(executor.submit(
            newSampler(root, state, finishTime, samplesLimit, samplesBatch,
                       selector)));
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
      newSampler(root, state, finishTime, samplesLimit, samplesBatch, selector)
          .run();
    }

    int playerNum = state.getPlayer();
    if (playerNum < 0 || playerNum > 1) {
      throw new RuntimeException("Player called on random move.");
    }
    boolean player = (playerNum == 0);
    Node<S, M> bestNode = null;
    double bestValue = player ? -1000 : 1000;
    for (Node<S, M> node : root.getChildren()) {
      if (bestNode == null ||
          (player ? (node.getPayoff() > bestValue)
              : (node.getPayoff() < bestValue))) {
        bestNode = node;
        bestValue = node.getPayoff();
      }
    }

    report = String.format(
        "Move: %s%n%s%n",
        state.moveToString(bestNode.getMove()),
        root.toStringNested(state, 16));

    return bestNode.getMove();
  }

  @Override
  public final void setMaxWorkers(int workers) {
    this.workers = workers;
  }

  @Override
  public void setMaxSamples(long maxSamples) {
    this.samplesLimit = maxSamples;
  }

  @Override
  public final void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  @Override
  public void addSolver(Solver<S, M> solver) {
    this.solver = solver;
    this.nodeContext.solver = solver;
  }

  public void setSelector(MoveSelector<S, M> selector) {
    this.selector = selector;
  }

  public void setSamplesBatch(int samplesBatch) {
    this.samplesBatch = samplesBatch;
  }

  public void setFindExact(boolean exact) {
    this.nodeContext.propagateExact = exact;
  }

  @Override
  public String getReport() {
    return report;
  }

  abstract protected Node<S, M> getRoot(S state);

  protected Sampler<S, M> newSampler(
      Node<S, M> root, S state, long finishTime, long samplesLimit, int
      samplesBatch,
      MoveSelector<S, M> selector) {
    Sampler<S, M> sampler = new Sampler<>(
        root, state, finishTime, samplesLimit, samplesBatch, selector);
    if (solver != null)
      sampler.setSolver(solver);
    return sampler;
  }

  protected long getCurrentTime() {
    return System.currentTimeMillis();
  }

}
