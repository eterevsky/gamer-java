package gamer.gomoku;

import static gamer.gomoku.Gomoku.POINTS;
import static gamer.gomoku.Gomoku.SIZE;
import static java.lang.Math.max;
import static java.lang.Math.min;

import gamer.def.IllegalMoveException;
import gamer.def.Move;
import gamer.def.Position;
import gamer.def.TerminalPositionException;
import gamer.util.GameStatusInt;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public final class GomokuState implements Position<GomokuState, GomokuMove> {
  private final BitSet marked;
  private final BitSet markedx;
  private final int status;

  @Override
  public GomokuState play(GomokuMove move) {
    if (isTerminal()) {
      throw new TerminalPositionException();
    }

    if (marked.get(move.point)) {
      throw new IllegalMoveException(this, move, "point is not empty");
    }

    return new GomokuState(this, move);
  }

  @Override
  public boolean isTerminal() {
    return GameStatusInt.isTerminal(status);
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
      throw new TerminalPositionException();

    int i;
    do {
      i = random.nextInt(POINTS);
    } while (marked.get(i));

    return GomokuMove.of(i);
  }

  GomokuState() {
    marked = new BitSet(POINTS);
    markedx = new BitSet(POINTS);
    status = GameStatusInt.init();
  }

  private GomokuState(GomokuState other, GomokuMove move) {
    marked = (BitSet) other.marked.clone();
    marked.set(move.point);
    if (other.getPlayerBool()) {
      markedx = (BitSet) other.markedx.clone();
      markedx.set(move.point);
    } else {
      markedx = other.markedx;
    }
    status = updateStatus(other.getPlayerBool(), move);
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
      status = GameStatusInt.switchPlayer(status);

    if (won) {
      status = GameStatusInt.setPayoff(status, player ? 1 : -1);
    } else if (marked.nextClearBit(0) == POINTS) {
      status = GameStatusInt.setPayoff(status, 0);
    }
    return status;
  }

  public String moveToString(GomokuMove move) {
    return move.toString();
  }

  @Override
  public int getPlayer() {
    return getPlayerBool() ? 0 : 1;
  }

  @Override
  public boolean getPlayerBool() {
    return GameStatusInt.getPlayerBool(status);
  }

  @Override
  public int getPayoff(int player) {
    return GameStatusInt.getPayoff(status, player);
  }

  @Override
  public GomokuMove parseMove(String moveStr) {
    return GomokuMove.of(moveStr);
  }

}
