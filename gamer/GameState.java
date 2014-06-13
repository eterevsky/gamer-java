package gamer;

import java.util.List;

public interface GameState<G extends Game> extends Cloneable {
  public boolean isTerminal();

  // True: first player's turn.
  public boolean isFirstPlayersTurn();

  // +1 / 0 / -1
  public int getResult() throws GameException;

  public List<Move<G>> getAvailableMoves();

  public void play(Move<G> move) throws GameException;
}
