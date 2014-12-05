package gamer.players;

import gamer.def.ComputerPlayer;
import gamer.def.Move;
import gamer.def.Position;
import gamer.def.Solver;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomPlayer<P extends Position<P, M>, M extends Move>
    implements ComputerPlayer<P, M> {
  private Random random = null;

  @Override
  public void addSolver(Solver<P> solver) {}

  @Override
  public void setMaxSamples(long maxSamples) {}

  @Override
  public void setMaxWorkers(int maxWorkers) {}

  @Override
  public void setRandom(Random random) {
    this.random = random;
  }

  @Override
  public void setTimeout(long timeout) {}

  @Override
  public String getName() {
    return "RandomPlayer";
  }

  @Override
  public String getReport() {
    return "";
  }

  @Override
  public boolean isHuman() {
    return false;
  }

  @Override
  public M selectMove(P position) {
    return position.getRandomMove(
        random == null ? ThreadLocalRandom.current() : random);
  }
}
