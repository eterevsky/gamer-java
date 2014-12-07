package gamer.def;

public interface Solver<P extends Position<P, M>, M extends Move> {
  public static class Result<M extends Move> {
    public int payoff;
    public int moves;
    public M move;

    public Result(int payoff, int moves, M move) {
      this.payoff = payoff;
      this.moves = moves;
      this.move = move;
    }
  }

  // null in case result is unknown.
  Result<M> solve(P position);
}
