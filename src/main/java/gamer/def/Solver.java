package gamer.def;

public interface Solver<P extends Position<?, ?>> {
  public static class Result {
    public int payoff;
    public int moves;

    public Result(int payoff, int moves) {
      this.payoff = payoff;
      this.moves = moves;
    }
  }

  // null in case result is unknown.
  Result solve(P position);
}
