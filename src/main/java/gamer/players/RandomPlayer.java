package gamer.players;

import gamer.def.ComputerPlayer;
import gamer.def.Move;
import gamer.def.Position;
import gamer.def.Solver;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomPlayer<P extends Position<P, M>, M extends Move>
    implements ComputerPlayer<P, M> {
  private Random random = null;

  @Override
  public void setMaxWorkers(int maxWorkers) {}

  @Override
  public void setMaxSamples(long maxSamples) {}

  @Override
  public void setTimeout(long timeout) {}

  @Override
  public void addSolver(Solver<P, M> solver) {}

  @Override
  public String getReport() {
    return "random move";
  }

  @Override
  public String getName() {
    return "RandomPlayer";
  }

  @Override
  public M selectMove(P position) {
    Random rng = random;
    if (rng == null) { rng = ThreadLocalRandom.current(); }
    List<M> moves = position.getMoves();
    return moves.get(rng.nextInt(moves.size()));
  }
}
