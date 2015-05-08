package gamer.gomoku;

import gamer.def.IllegalMoveException;
import gamer.def.Position;
import gamer.def.TerminalPositionException;
import gamer.util.GameStatusInt;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public final class GomokuState implements Position<GomokuState, GomokuMove> {
  private final int size;
  private final Limits limits;
  private final BitSet marked;
  private final BitSet markedx;
  private int status;

  GomokuState(int size, Limits limits) {
    this.size = size;
    this.limits = limits;
    marked = new BitSet(size * size);
    markedx = new BitSet(size * size);
    status = GameStatusInt.init();
  }

  private GomokuState(GomokuState other) {
    size = other.size;
    limits = other.limits;
    marked = (BitSet) other.marked.clone();
    markedx = (BitSet) other.markedx.clone();
    status = other.status;
  }

  @Override
  public boolean getPlayerBool() {
    return GameStatusInt.getPlayerBool(status);
  }

  @Override
  public boolean isTerminal() {
    return GameStatusInt.isTerminal(status);
  }

  @Override
  public int getPayoff(int player) {
    return GameStatusInt.getPayoff(status, player);
  }

  @Override
  public List<GomokuMove> getMoves() {
    List<GomokuMove> moves = new ArrayList<>();
    for (int i = 0; i < size * size; i++) {
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
      i = random.nextInt(size * size);
    } while (marked.get(i));

    return GomokuMove.of(i);
  }

  @Override
  public void play(GomokuMove move) {
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

    updateStatus(getPlayerBool(), move);
  }

  @Override
  public String moveToString(GomokuMove move) {
    return move.toString(size);
  }

  @Override
  public GomokuMove parseMove(String moveStr) {
    return GomokuMove.of(moveStr, size);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < size * size; i++) {
      if (marked.get(i)) {
        builder.append(markedx.get(i) ? 'X' : 'O');
      } else {
        builder.append('.');
      }

      builder.append(i % size == size - 1 ? '\n' : ' ');
    }

    return builder.toString();
  }

  @Override
  public GomokuState clone() {
    return new GomokuState(this);
  }

  private void updateStatus(boolean player, GomokuMove move) {
    int point = move.point;
    boolean won = checkLine(point, limits.w[point], limits.e[point], 1, player)
        || checkLine(point, limits.n[point], limits.s[point], size, player)
        || checkLine(
            point, limits.nw[point], limits.se[point], size + 1, player)
        || checkLine(
            point, limits.ne[point], limits.sw[point], size - 1, player);

    status = GameStatusInt.init();
    if (!player)
      status = GameStatusInt.switchPlayer(status);
    if (won) {
      status = GameStatusInt.setPayoff(status, player ? 1 : -1);
    } else if (marked.nextClearBit(0) == size * size) {
      status = GameStatusInt.setPayoff(status, 0);
    }
  }

  private boolean checkLine(
      int center, int lo, int hi, int delta, boolean player) {
    int l = 1;
    for (int cell = center - delta; cell >= lo; cell -= delta) {
      if (!marked.get(cell) || markedx.get(cell) != player)
        break;
      l++;
    }

    for (int cell = center + delta; cell <= hi; cell += delta) {
      if (!marked.get(cell) || markedx.get(cell) != player)
        break;
      l++;
    }

    return l >= 5;
  }

}
