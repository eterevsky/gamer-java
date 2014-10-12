package gamer.def;

import java.util.List;
import java.util.Random;

// Immutable
public interface GameState<G extends Game> {
  boolean isTerminal();

  GameStatus status();

  List<? extends Move<G>> getMoves();

  Move<G> getRandomMove(Random random);

  GameState<G> play(Move<G> move);
}
