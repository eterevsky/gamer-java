package gamer.def;

/**
 * A move. Immutable.
 */
public interface Move {
  @Override
  int hashCode();
  
  @Override
  String toString();
}
