package gamer.def;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public interface GameState<G extends Game> extends Cloneable {
  boolean isTerminal();

  boolean getPlayer();

  GameResult getResult();

  List<Move<G>> getMoves();

  Move<G> getRandomMove(Random random);

  void play(Move<G> move);

  GameState<G> clone();
}
