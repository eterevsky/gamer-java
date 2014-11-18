package gamer.gomoku;

import static gamer.gomoku.Gomoku.POINTS;
import static gamer.gomoku.Gomoku.SIZE;
import static java.lang.Math.max;
import static java.lang.Math.min;

import gamer.def.GameException;
import gamer.def.GameStateMut;
import gamer.def.GameStatus;
import gamer.def.IllegalMoveException;
import gamer.def.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public final class GomokuStateMut implements GameStateMut<Gomoku> {
  private BitSet marked;
  private BitSet markedx;
  private GameStatus status;

  GomokuStateMut() {
    marked = new BitSet(POINTS);
    markedx = new BitSet(POINTS);
    status = GameStatus.FIRST_PLAYER;
  }

  private GomokuStateMut(GomokuStateMut other) {
    marked = (BitSet) other.marked.clone();
    markedx = (BitSet) other.markedx.clone();
    status = other.status;
  }

  public GameStatus status() {
    return status;
  }

  public GomokuStateMut play(Move<Gomoku> moveInt) {
    GomokuStateMut next = new GomokuStateMut(this);
    next.playInPlace(moveInt);
    return next;
  }

  public void playInPlace(Move<Gomoku> moveInt) {
    GomokuMove move = (GomokuMove) moveInt;

    if (status.isTerminal()) {
      throw new IllegalMoveException(this, move, "state is terminal");
    }

    if (marked.get(move.point)) {
      throw new IllegalMoveException(this, move, "point is not empty");
    }

    marked.set(move.point);
    if (status.getPlayer()) {
      markedx.set(move.point);
    }

    status = updateStatus(status.getPlayer(), move);
  }

  public void reset() {
    marked.clear();
    markedx.clear();
    status = GameStatus.FIRST_PLAYER;
  }

  public boolean isTerminal() {
    return status.isTerminal();
  }

  public List<Move<Gomoku>> getMoves() {
    List<Move<Gomoku>> moves = new ArrayList<>();
    for (int i = 0; i < POINTS; i++) {
      if (!marked.get(i)) {
        moves.add(GomokuMove.of(i));
      }
    }

    return moves;
  }

  public GomokuMove getRandomMove(Random random) {
    if (isTerminal())
      throw new GameException();

    int i;
    do {
      i = random.nextInt(POINTS);
    } while (marked.get(i));

    return GomokuMove.of(i);
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < POINTS; i++) {
      if (marked.get(i)) {
        if (markedx.get(i)) {
          builder.append('X');
        } else {
          builder.append('O');
        }
      } else {
        builder.append('.');
      }

      if (i % SIZE == SIZE - 1) {
        builder.append('\n');
      } else {
        builder.append(' ');
      }
    }

    return builder.toString();
  }

  private boolean checkLine(int center, int left, int right, int delta) {
    boolean player = markedx.get(center);
    int l = 1;
    for (int cell = center - delta; cell >= left; cell -= delta) {
      if (!marked.get(cell) || markedx.get(cell) != player)
        break;
      l++;
    }

    for (int cell = center + delta; cell <= right; cell += delta) {
      if (!marked.get(cell) || markedx.get(cell) != player)
        break;
      l++;
    }

    return l >= 5;
  }

  static int[] limLeft = new int[POINTS], limRight = new int[POINTS];
  static int[] limTop = new int[POINTS], limBottom = new int[POINTS];
  static int[] limLT = new int[POINTS], limRB = new int[POINTS];
  static int[] limRT = new int[POINTS], limLB = new int[POINTS];

  static {
    for (int cell = 0; cell < POINTS; cell++) {
      int x = cell % SIZE;
      int y = cell / SIZE;

      int left = min(x, 4);
      int right = min(SIZE - x - 1, 4);
      int top = min(y, 4);
      int bottom = min(SIZE - y - 1, 4);

      limLeft[cell] = cell - left;
      limRight[cell] = cell + right;
      limTop[cell] = cell - SIZE * top;
      limBottom[cell] = cell + SIZE * bottom;
      limLT[cell] = cell - (SIZE + 1) * min(left, top);
      limRB[cell] = cell + (SIZE + 1) * min(right, bottom);
      limRT[cell] = cell - (SIZE - 1) * min(right, top);
      limLB[cell] = cell + (SIZE - 1) * min(left, bottom);
    }
  }

  private GameStatus updateStatus(boolean player, GomokuMove move) {
    int cell = move.point;
    boolean won =
        checkLine(cell, limLeft[cell], limRight[cell], 1) ||
        checkLine(cell, limTop[cell], limBottom[cell], SIZE) ||
        checkLine(cell, limLT[cell], limRB[cell], SIZE + 1) ||
        checkLine(cell, limRT[cell], limLB[cell], SIZE - 1);

    if (won) {
      return player ? GameStatus.WIN : GameStatus.LOSS;
    } else if (marked.nextClearBit(0) == POINTS) {
      return GameStatus.DRAW;
    } else {
      return player ? GameStatus.SECOND_PLAYER : GameStatus.FIRST_PLAYER;
    }
  }

  public String moveToString(Move<Gomoku> move) {
    return move.toString();
  }

}
