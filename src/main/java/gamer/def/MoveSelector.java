package gamer.def;

/**
 * Select a single move for a position. Not thread-safe.
 */
public interface MoveSelector<P extends Position<P, M>, M extends Move> {
  M select(P position);

  MoveSelector<P, M> clone();
}
