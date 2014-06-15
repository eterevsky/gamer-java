package gamer;

import java.util.List;

public interface GameState<G extends Game> extends Cloneable {
  public boolean isTerminal();

  public boolean getPlayer();

  // +1 / 0 / -1
  public int getResult() throws GameException;

  public List<Move<G>> getAvailableMoves();

  public Move<G> getRandomMove();

  public void play(Move<G> move) throws GameException;

  public GameState<G> clone();
}
