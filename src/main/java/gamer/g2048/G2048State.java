package gamer.g2048;

import gamer.def.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
  }

  private int[] board = new int[16];
  private int score = 0;
  private State state = State.NEW_GAME;

  static class RandomSelector implements MoveSelector<G2048State, G2048Move> {
    @Override
    public G2048Move select(G2048State state) {
      return state.getRandomMove();
    }
  }

  @Override
  public int getPlayer() {
    switch (state) {
      case NEW_GAME: case RANDOM: return -1;
      case PLAYER: return 0;
      default: throw new TerminalPositionException();
    }
  }

  @Override
  public boolean isTerminal() {
    return state == State.FINISHED;
  }

  @Override
  public int getPayoff(int player) {
    if (!isTerminal()) {
      throw new TerminalPositionException();
    }

    return score;
  }

  @Override
  public List<G2048Move> getMoves() {
    switch (state) {
      case NEW_GAME: case RANDOM: return generateRandomMoves();
      case PLAYER: return G2048Move.PLAYER_MOVES;
      default: throw new TerminalPositionException();
    }
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

  private int emptyTiles() {
    int count = 0;
    for (int i = 0; i < 16; i++) {
      if (board[i] == 0) {
        count++;
      }
    }
    return count;
  }

  @Override
  public G2048Move getRandomMove() {
    Random random = ThreadLocalRandom.current();
    switch (state) {
      case NEW_GAME: case RANDOM:
        int rand = random.nextInt(10 * emptyTiles());
        int tile = rand / 10;
        int value = rand % 10 == 0 ? 4 : 2;
        return G2048Move.of(tile, value);
      case PLAYER:
        return G2048Move.PLAYER_MOVES.get(random.nextInt(4));
      default: throw new TerminalPositionException();
    }
  }

  @Override
  public void play(G2048Move move) {
    switch (state) {
      case NEW_GAME:
        if (!move.isRandom()) {
          throw new IllegalMoveException(this, move, "Expecting random move.");
        }
        board[move.tile] = move.value;
        state = State.RANDOM;
        break;

      case RANDOM:
        if (!move.isRandom()) {
          throw new IllegalMoveException(this, move, "Expecting random move.");
        }
        board[move.tile] = move.value;
        state = State.PLAYER;
        break;

      case PLAYER:
        if (move.isRandom()) {
          throw new IllegalMoveException(this, move, "Expecting player move.");
        }

        shiftTiles(move);

        break;

      default:
        throw new TerminalPositionException();
    }
  }

  private void shiftTiles(G2048Move move) {
     int startTile = move.startRow;
     for (int i = 0; i < 4; i++) {
       int toTile = startTile;
       int fromTile = startTile;
       int lastTileValue = 0;

       for (int j = 0; j < 4; j++) {
         int value = board[fromTile];
         if (value != 0) {
           board[fromTile] = 0;
           if (value == lastTileValue) {
             board[toTile] = 2 * value;
             lastTileValue = 0;
           } else {
             board[toTile] = value;
             lastTileValue = value;
           }
           toTile += move.deltaTile;
         }

         fromTile += move.deltaTile;
       }

       startTile += move.deltaRow;
     }
  }

  int get(int tile) {
    return board[tile];
  }

  int get(String tileStr) {
    return board[G2048.getInstance().board.parseTile(tileStr)]
  }

  public G2048Move parseMove(String moveStr) {
    return G2048Move.parse(moveStr);
  }

  public G2048State clone() {
    G2048State other = new G2048State();
    other.board = this.board.clone();
    other.score = this.score;
    other.state = this.state;

    return other;
  }

  @Override
  public String toString() {
    return "";
  }
}
