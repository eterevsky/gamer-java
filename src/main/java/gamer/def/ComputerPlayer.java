package gamer.def;

import java.util.Random;
import java.util.concurrent.ExecutorService;

public interface ComputerPlayer<P extends Position<P, M>, M extends Move>
    extends Player<P, M> {
  void setRandom(Random random);
  void setMaxWorkers(int maxWorkers);
  void setMaxSamples(long maxSamples);
  void setTimeout(long timout);

  void addSolver(Solver<P> solver);

  String getReport();
}
