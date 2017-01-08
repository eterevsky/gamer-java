package gamer.gomoku;

import gamer.def.IllegalMoveException;
import gamer.def.Position;
import gamer.def.TerminalPositionException;
import gamer.util.GameStatusInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class GomokuState implements Position<GomokuState, GomokuMove> {
  private final int size;
  private final Limits limits;
  private byte[] board;

  private int status;

  GomokuState(int size, Limits limits) {
    this.size = size;
    this.limits = limits;
    board = new byte[size * size];
    status = GameStatusInt.init();
  }

  @Override public boolean getPlayerBool() {
    return GameStatusInt.getPlayerBool(status);
  }

  @Override public boolean isTerminal() {
    return GameStatusInt.isTerminal(status);
  }

  @Override public int getPayoff(int player) {
    return GameStatusInt.getPayoff(status, player);
  }

  @Override public List<GomokuMove> getMoves() {
    List<GomokuMove> moves = new ArrayList<>();
    for (int i = 0; i < board.length; i++) {
      if (board[i] == 0) {
        moves.add(GomokuMove.of(i));
      }
    }

    return moves;
  }

  /* package */ void playRandomMove(Random random) {
    if (isTerminal())
      throw new TerminalPositionException();

    int i;
    do {
      // This is faster than nextInt(board_len), though slightly biased
      i = (random.nextInt() & 0x7FFFFFFF) % board.length;
    } while (board[i] != 0);

    play(GomokuMove.of(i));
  }

  @Override public void play(GomokuMove move) {
    if (isTerminal()) {
      throw new IllegalMoveException(this, move, "state is terminal");
    }

    if (board[move.point] != 0) {
      throw new IllegalMoveException(this, move, "point is not empty");
    }

    board[move.point] = getPlayerBool() ? (byte)1 : (byte)2;
    updateStatus(getPlayerBool(), move.point);
  }

  @Override public String moveToString(GomokuMove move) {
    return move.toString(size);
  }

  @Override public GomokuMove parseMove(String moveStr) {
    return GomokuMove.of(moveStr, size);
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < board.length; i++) {
      if (board[i] != 0) {
        builder.append((board[i] == 1) ? 'X' : 'O');
      } else {
        builder.append('.');
      }

      builder.append(i % size == size - 1 ? '\n' : ' ');
    }

    return builder.toString();
  }

  @Override public GomokuState clone() {
    try {
      GomokuState result = (GomokuState) super.clone();
      result.board = board.clone();
      return result;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError(e);
    }
  }

  /**
   * Get contents of a point.
   * @param point String representation of a point.
   * @return 0 - empty, 1 - X (first player), 2 - O (second player)
   */
  public int get(String point) {
    GomokuMove move = GomokuMove.of(point, size);
    return board[move.point];
  }

  private void updateStatus(boolean player, int point) {
    boolean won = checkLine(point, limits.w[point], limits.e[point], 1, player)
        || checkLine(point, limits.n[point], limits.s[point], size, player)
        || checkLine(
            point, limits.nw[point], limits.se[point], size + 1, player)
        || checkLine(
            point, limits.ne[point], limits.sw[point], size - 1, player);

    status = GameStatusInt.init();
    if (player)
      status = GameStatusInt.switchPlayer(status);
    if (won) {
      status = GameStatusInt.setPayoff(status, player ? 1 : -1);
      return;
    }

    for (int i = 0; i < board.length; i++) {
      if (board[i] == 0)
        return;
    }
    status = GameStatusInt.setPayoff(status, 0);
  }

  private boolean checkLine(
      int center, int lo, int hi, int delta, boolean player) {
    int l = 1;
    byte v = player ? (byte)1 : (byte)2;
    for (int cell = center - delta; cell >= lo; cell -= delta) {
      if (board[cell] != v)
        break;
      l++;
    }

    for (int cell = center + delta; cell <= hi; cell += delta) {
      if (board[cell] != v)
        break;
      l++;
    }

    return l >= 5;
  }
}
