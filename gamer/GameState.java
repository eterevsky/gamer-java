package gamer;

import java.util.List;

public interface GameState<G extends Game> extends Cloneable {
  boolean isTerminal();

  boolean getPlayer();

  // +1 / 0 / -1
  GameResult getResult() throws GameException;

  List<Move<G>> getAvailableMoves();

  Move<G> getRandomMove();

  void play(Move<G> move) throws GameException;

  GameState<G> clone();
}
