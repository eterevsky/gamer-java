package gamer.def;

public interface GameStateMut<G extends Game> extends GameState<G> {
  void playInPlace(Move<G> move);

  void reset();
}
