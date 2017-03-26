package gamer.def;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The class describing a game position.
 */
public interface State<S extends State<S, M>, M extends Move>
    extends Cloneable {
  Game<S, M> getGame();

  /**
   * Get the player number of a player to move. The first player to move in the
   * beginning of the game has number 0.
   *
   * @return Player number or -1 for the probabilistic move.
   */
  default int getPlayer() {
    return getPlayerBool() ? 0 : 1;
  }

  /**
   * True if the current player is player 0.
   * Works for games with two players without chance.
   *
   * @return true for player 0, false for player 1.
   * @throws UnsupportedOperationException if the number of players ≠ 2.
   */
  default boolean getPlayerBool() {
    throw new UnsupportedOperationException("getPlayerBool() not supported");
  }

  /**
   * Check whether the current position is terminal.
   *
   * @return true if current position is terminal
   */
  boolean isTerminal();

  /**
   * Check whether the move in this state is random.
   *
   * @return true if the next move is random.
   */
  default boolean isRandom() {
    return getPlayer() < 0;
  }

  /**
   * Get payoff for a terminal position, for the specified player.
   *
   * @return The greater the better, 0 means draw.
   * @throws TerminalPositionException if called for a non-terminal position.
   */
  int getPayoff(int player);

  /**
   * Get the list of legal moves in the current position. Can be not implemented
   * for some games, where the space of all moves is too large.
   */
  List<M> getMoves();

  /**
   * Get a random move.
   *
   * The default implementation is inefficient and relies on getMoves().
   */
  default M getRandomMove() {
    List<M> all_moves = getMoves();
    return all_moves.get(ThreadLocalRandom.current().nextInt(all_moves.size()));
  }

  /**
   * Apply a move to the current position.
   */
  void play(M move);
  
  default void play(String moveStr) {
    this.play(this.parseMove(moveStr));
  }

  default String moveToString(M move) {
    return move.toString();
  }

  M parseMove(String moveStr);

  S clone();

  String toString();
}
