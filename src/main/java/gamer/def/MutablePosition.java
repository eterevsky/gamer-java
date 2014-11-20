package gamer.def;

public interface GameStateMut<M extends Move> extends GameState<M> {
  void apply(M move);

  void reset();
}
