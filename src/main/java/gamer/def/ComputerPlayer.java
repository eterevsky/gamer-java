package gamer.def;

import java.util.Random;

public interface ComputerPlayer<P extends Position<P, M>, M extends Move>
    extends Player<P, M> {
  default boolean isExternal() {
    return false;
  }

  // void setRandom(Random random);

  void setMaxWorkers(int maxWorkers);

  void setMaxSamples(long maxSamples);

  void setTimeout(long timout);

  void addSolver(Solver<P, M> solver);

  String getReport();
}
