package gamer.def;

public interface PositionMut<P extends PositionMut<P, M>, M extends Move>
    extends Position<P, M> {
  void apply(M move);

  void reset();
}
