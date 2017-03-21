package gamer.def;

public interface ComputerPlayer<P extends State<P, M>, M extends Move>
    extends Player<P, M> {
  default boolean isExternal() {
    return false;
  }

  void setMaxWorkers(int maxWorkers);

  void setMaxSamples(long maxSamples);

  void setTimeout(long timout);

  default void addSolver(Solver<P, M> solver) {
    throw new UnsupportedOperationException("Solvers are not supported.");
  }

  String getReport();
}
