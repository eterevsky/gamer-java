package gamer.def;

import java.util.List;

public interface GameState<G extends Game> extends Cloneable {
  boolean isTerminal();

  boolean getPlayer();

  GameResult getResult();

  List<Move<G>> getMoves();

  Move<G> getRandomMove();

  void play(Move<G> move);

  GameState<G> clone();
}
