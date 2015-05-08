package gamer.def;

import java.util.List;
import java.util.Random;

/**
 * The class describing a game position.
 */
public interface Position<P extends Position<P, M>, M extends Move> {
  /**
   * Get the player number of a player to move. The first player to move in the 
   * beginning of the game has number 0. 
   * @return Player number or -1 for the probabilistic move.
   */
  int getPlayer();

  /**
   * True if the current player is player 0.
   * Works for games with two players without chance.
   * @return true for player 0, false for player 1.
   * @throws UnsupportedOperationException if the number of players ≠ 2.
   */
  default boolean getPlayerBool() {
    throw new UnsupportedOperationException("getPlayerBool() not supported");
  }

  /**
   * Check whether the current position is terminal.
   * @return true if current position is terminal
   */
  boolean isTerminal();

  /**
   * Get payoff for a terminal position, for the specified player.
   * @return The greater the better, 0 means draw.
   * @throws TerminalPositionException if called for a non-terminal position.
   */
  int getPayoff(int player);

  /**
   * Get the list of legal moves in the current position.
   */
  List<M> getMoves();

  /**
   * Get a single random move in the current position. If the game is with
   * chanсe and the next move is probabilistic, the result of getRandomMove
   * conforms the moves distribution. 
   */
  M getRandomMove(Random random);

  /**
   * Apply a move to the current position.
   */
  void play(M move);
  
  void play(String moveStr) {
    this.play(this.parseMove(moveStr));
  }

  default String moveToString(M move) {
    return move.toString();
  }

  M parseMove(String moveStr);
	
  /**
   * Make a copy of current position as a mutable position. The position is
   * cloned even if the current position is already mutable. 
   */
	<P1 extends PositionMut<P1, M>> P1 toMutable();

  /**
   * Make a copy of current position, if possible, immutable. May return 
   * current object if it is already immutable.
   */
	<P1 extends PositionMut<P1, M>> P1 toImmutable();
}
