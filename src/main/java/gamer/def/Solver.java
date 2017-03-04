package gamer.def;

/**
 * Provide exact solutions (optimal moves) for a subset of possible game
 * positions.
 */
public interface Solver<P extends Position<P, M>, M extends Move> {
  class Result<M extends Move> {
    public int payoff;
    public int moves;
    public M move;

    public Result(int payoff, int moves, M move) {
      this.payoff = payoff;
      this.moves = moves;
      this.move = move;
    }
  }

  /**
   * @return null in case result is unknown.
   */
  Result<M> solve(P position);
}
