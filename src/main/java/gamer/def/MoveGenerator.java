package gamer.def;

import java.util.List;

/**
 * Generate a set of moves for a position. Should be stateless and thread-safe.
 */
public interface MoveGenerator<P extends State<P, M>, M extends Move> {
  List<M> generate(P position);
}
