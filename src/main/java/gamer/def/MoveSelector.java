package gamer.def;

/**
 * Select a single move for a position. Should be stateless and thread-safe.
 */
public interface MoveSelector<P extends State<P, M>, M extends Move> {
  M select(P position);
}
