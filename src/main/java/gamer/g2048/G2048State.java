package gamer.g2048;

import gamer.def.Position;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


/**
 * The class describing a game position.
 */
public final class G2048State implements Position<G2048State, G2048Move> {
  private enum State {
    NEW_GAME,
    RANDOM,
    PLAYER,
    FINISHED
  };

  private int[16] board;
  private int score = 0;
  private State state;

  @Override
  public int getPlayer() {
    switch (state) {
      case NEW_GAME: case RANDOM: return -1;
      case PLAYER: return 0;
      case FINISHED: throw new TerminalPositionException();
    }
  }

  @Override
  public boolean isTerminal() {
    return state == FINISHED;
  }

  @Override
  public int getPayoff(int player) {
    if (!isTerminal()) {
      throw new TerminalPositionException();
    }

    return score;
  }

  private List<G2048Move> generateRandomMoves() {
    List<G2048Move> moves = new ArrayList<>();
    for (int i = 0; i < 16; i++) {
      if (board[i] == 0) {
        moves.add(G2048Move.of(i, 2));
        moves.add(G2048Move.of(i, 4));
      }
    }

    return moves;
  }

  @Override
  public List<G2048Move> getMoves() {
    switch (state) {
      case NEW_GAME: case RANDOM: return generateRandomMoves();
      case PLAYER: return G2048Move.PLAYER_MOVES;
      case FINISHED: throw new TerminalPositionException();
    }
  }

  @Override
  public void playRandomMove() {
    switch (state) {
      case NEW_GAME: case RANDOM:
        int count_empty = Colletions.frequency(
      case PLAYER:
        play(G2048Move.PLAYER_MOVES[ThreadLocalRandom.current().nextInt(4)]);
        break;
      case FINISHED: throw new TerminalPositionException();
    }
  }

  @Override
  public void play(G2048Move move) {
    switch (state) {
      case NEW_GAME:
        if (!move.isRandom()) {
          throw new GameException();
        }
    }
  }

  M parseMove(String moveStr);

  P clone();

  String toString();
}
