package gamer.def;

/**
 * Select a single move for a position. Should be stateless and thread-safe.
 */
public interface MoveSelector<P extends Position<P, M>, M extends Move> {
  String getName();

  M select(P position);
}
