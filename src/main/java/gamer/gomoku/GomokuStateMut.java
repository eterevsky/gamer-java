package gamer.gomoku;

import static gamer.gomoku.Gomoku.POINTS;
import static gamer.gomoku.Gomoku.SIZE;
import static java.lang.Math.max;
import static java.lang.Math.min;

import gamer.def.IllegalMoveException;
import gamer.def.Move;
import gamer.def.PositionMut;
import gamer.def.TerminalPositionException;
import gamer.util.GameStatusInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public final class GomokuStateMut
    implements PositionMut<GomokuStateMut, GomokuMove> {
  private BitSet marked;
  private BitSet markedx;
  private int status;

  GomokuStateMut() {
    marked = new BitSet(POINTS);
    markedx = new BitSet(POINTS);
    status = GameStatusInt.init();
  }

  private GomokuStateMut(GomokuStateMut other) {
    marked = (BitSet) other.marked.clone();
    markedx = (BitSet) other.markedx.clone();
    status = other.status;
  }

  public boolean isTerminal() {
    return GameStatusInt.isTerminal(status);
  }

  @Override
  public GomokuStateMut play(GomokuMove move) {
    GomokuStateMut next = new GomokuStateMut(this);
    next.apply(move);
    return next;
  }

  @Override
  public void apply(GomokuMove move) {
    if (isTerminal()) {
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

  public List<GomokuMove> getMoves() {
    List<GomokuMove> moves = new ArrayList<>();
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

  private int updateStatus(boolean player, GomokuMove move) {
    int cell = move.point;
    boolean won =
        checkLine(cell, limLeft[cell], limRight[cell], 1) ||
        checkLine(cell, limTop[cell], limBottom[cell], SIZE) ||
        checkLine(cell, limLT[cell], limRB[cell], SIZE + 1) ||
        checkLine(cell, limRT[cell], limLB[cell], SIZE - 1);


    int status = GameStatusInt.init();
    if (!player)
      status = GamerStatusInt.switchPlayer(status);
    if (won) {
      status = GamerStatusInt.setPayoff(status, player ? 1 : -1);
    } else if (marked.nextClearBit(0) == POINTS) {
      status = GamerStatusInt.setPayoff(status, 0);
    }

    return status;
  }

  public String moveToString(GomokuMove move) {
    return move.toString();
  }

}
