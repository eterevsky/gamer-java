package gamer.gomoku;

import gamer.def.GameException;
import gamer.def.GameState;
import gamer.def.GameStatus;
import gamer.def.IllegalMoveException;
import gamer.def.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public final class GomokuState implements GameState<Gomoku> {
  private final BitSet marked;
  private final BitSet markedx;
  private GameStatus status;

  GomokuState() {
    marked = new BitSet(Gomoku.CELLS);
    markedx = new BitSet(Gomoku.CELLS);
    status = GameStatus.FIRST_PLAYER;
  }

  private GomokuState(GomokuState other) {
    marked = (BitSet) other.marked.clone();
    markedx = (BitSet) other.markedx.clone();
    status = other.status;
  }

  @Override
  public GomokuState clone() {
    return new GomokuState(this);
  }

  public void play(Move<Gomoku> moveInt) {
    GomokuMove move = (GomokuMove) moveInt;

    if (status.isTerminal()) {
      throw new IllegalMoveException(this, move, "state is terminal");
    }

    if (marked.get(move.cell)) {
      throw new IllegalMoveException(this, move, "cell is not empty");
    }

    if (move.player != status.getPlayer()) {
      throw new IllegalMoveException(this, move, "wrong player");
    }

    marked.set(move.cell);
    if (move.player) {
      markedx.set(move.cell);
    }

    updateStatus(move.cell, move.player);
  }

  public GameStatus status() {
    return status;
  }

  public boolean isTerminal() {
    return status.isTerminal();
  }

  public List<Move<Gomoku>> getMoves() {
    List<Move<Gomoku>> moves = new ArrayList<>();
    for (int i = 0; i < Gomoku.CELLS; i++) {
      if (!marked.get(i)) {
        moves.add(GomokuMove.of(i, status.getPlayer()));
      }
    }

    return moves;
  }

  public GomokuMove getRandomMove(Random random) {
    if (isTerminal())
      throw new GameException();

    int i;
    do {
      i = random.nextInt(Gomoku.CELLS);
    } while (marked.get(i));

    return GomokuMove.of(i, status.getPlayer());
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append('\n');
    for (int i = 0; i < Gomoku.SIZE * Gomoku.SIZE; i++) {
      if (marked.get(i)) {
        if (markedx.get(i)) {
          builder.append('X');
        } else {
          builder.append('O');
        }
      } else {
        builder.append('.');
      }

      if (i % Gomoku.SIZE == Gomoku.SIZE - 1) {
        builder.append('\n');
      } else {
        builder.append(' ');
      }
    }

    return builder.toString();
  }

  private boolean checkLine(int from, int to, int delta, boolean player) {
    int filled = 0;
    for (int i = from; i <= to; i += delta) {
      if (marked.get(i) && markedx.get(i) == player) {
        filled += 1;
        if (filled == 5)
          return true;
      } else {
        filled = 0;
      }
    }

    return false;
  }

  private void updateStatus(int cell, boolean player) {
    int x = cell % Gomoku.SIZE;
    int y = cell / Gomoku.SIZE;

    status = status.otherPlayer();

    int left = Math.min(x, 4);
    int right = Math.min(Gomoku.SIZE - x - 1, 4);
    int top = Math.min(y, 4);
    int bottom = Math.min(Gomoku.SIZE - y - 1, 4);

    boolean won = checkLine(cell - left, cell + right, 1, player)
               || checkLine(cell - top * Gomoku.SIZE,
                            cell + bottom * Gomoku.SIZE,
                            Gomoku.SIZE,
                            player)
               || checkLine(cell - Math.min(left, top) * (Gomoku.SIZE + 1),
                            cell + Math.min(right, bottom) * (Gomoku.SIZE + 1),
                            Gomoku.SIZE + 1,
                            player)
               || checkLine(cell - Math.min(right, top) * (Gomoku.SIZE - 1),
                            cell + Math.min(left, bottom) * (Gomoku.SIZE - 1),
                            Gomoku.SIZE - 1,
                            player);

    if (won) {
      status = player ? GameStatus.WIN : GameStatus.LOSS;
    } else if (marked.nextClearBit(0) == Gomoku.CELLS) {
      status = GameStatus.DRAW;
    }
  }

}
