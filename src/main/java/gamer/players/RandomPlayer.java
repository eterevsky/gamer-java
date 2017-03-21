package gamer.players;

import gamer.def.ComputerPlayer;
import gamer.def.Move;
import gamer.def.State;
import gamer.def.Solver;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomPlayer<S extends State<S, M>, M extends Move>
    implements ComputerPlayer<S, M> {
  private Random random = null;

  @Override
  public void setMaxWorkers(int maxWorkers) {}

  @Override
  public void setMaxSamples(long maxSamples) {}

  @Override
  public void setTimeout(long timeout) {}

  @Override
  public void addSolver(Solver<S, M> solver) {}

  @Override
  public String getReport() {
    return "random move";
  }

  @Override
  public String getName() {
    return "RandomPlayer";
  }

  @Override
  public M selectMove(S position) {
    Random rng = random;
    if (rng == null) { rng = ThreadLocalRandom.current(); }
    List<M> moves = position.getMoves();
    return moves.get(rng.nextInt(moves.size()));
  }
}
