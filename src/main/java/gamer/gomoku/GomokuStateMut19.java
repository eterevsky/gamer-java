package gamer.gomoku;

import static java.lang.Math.min;

import gamer.def.IllegalMoveException;
import gamer.def.PositionMut;
import gamer.def.TerminalPositionException;
import gamer.util.GameStatusInt;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public final class GomokuStateMut19
    implements PositionMut<GomokuStateMut19, GomokuMove> {
  private BitSet marked;
  private BitSet markedx;
  private int status;
	
	private static final int SIZE = 19;
	private static final int POINTS = SIZE * SIZE;

  GomokuStateMut19() {
    marked = new BitSet(POINTS);
    markedx = new BitSet(POINTS);
    status = GameStatusInt.init();
  }

  private GomokuStateMut19(GomokuStateMut19 other) {
    marked = (BitSet) other.marked.clone();
    markedx = (BitSet) other.markedx.clone();
    status = other.status;
  }

  @Override
  public boolean isTerminal() {
    return GameStatusInt.isTerminal(status);
  }

  @Override
  public GomokuStateMut19 play(GomokuMove move) {
    GomokuStateMut19 next = new GomokuStateMut19(this);
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
    if (getPlayerBool()) {
      markedx.set(move.point);
    }

    status = updateStatus(getPlayerBool(), move);
  }

  @Override
  public void reset() {
    marked.clear();
    markedx.clear();
    status = GameStatusInt.init();
  }

  @Override
  public List<GomokuMove> getMoves() {
    List<GomokuMove> moves = new ArrayList<>();
    for (int i = 0; i < POINTS; i++) {
      if (!marked.get(i)) {
        moves.add(GomokuMove.of(i));
      }
    }

    return moves;
  }

  @Override
  public GomokuMove getRandomMove(Random random) {
    if (isTerminal())
      throw new TerminalPositionException();

    int i;
    do {
      i = random.nextInt(POINTS);
    } while (marked.get(i));

    return GomokuMove.of(i);
  }

  @Override
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

  private int updateStatus(boolean player, GomokuMove move) {
    int cell = move.point;
    boolean won = checkLine(cell, limLeft[cell], limRight[cell], 1)
        || checkLine(cell, limTop[cell], limBottom[cell], SIZE)
        || checkLine(cell, limLT[cell], limRB[cell], SIZE + 1)
        || checkLine(cell, limRT[cell], limLB[cell], SIZE - 1);


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

  @Override
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
    return GomokuMove.of(moveStr, SIZE);
  }

  static final int[] limLeft, limRight;
  static final int[] limTop, limBottom;
  static final int[] limLT, limRB;
  static final int[] limRT, limLB;

	static {
		Gomoku gomoku19 = Gomoku.getInstance(19);
    limLeft = gomoku19.limLeft;
    limRight = gomoku19.limRight;
    limTop = gomoku19.limTop;
    limBottom = gomoku19.limBottom;
    limLT = gomoku19.limLT;
    limRB = gomoku19.limRB;
    limRT = gomoku19.limRT;
    limLB = gomoku19.limLB;
  }
}
