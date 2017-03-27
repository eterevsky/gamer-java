package gamer.gomoku;

import gamer.def.IllegalMoveException;
import gamer.def.MoveSelector;
import gamer.def.State;
import gamer.def.TerminalPositionException;
import gamer.util.GameStatusInt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.concurrent.ThreadLocalRandom;

public final class GomokuState implements State<GomokuState, GomokuMove> {
  private final int size;
  private final Limits limits;
  private byte[] board;

  private int status;

  static class RandomSelector implements MoveSelector<GomokuState, GomokuMove> {
    private final int boardLen;

    RandomSelector(int size) {
      boardLen = size * size;
    }

    @Override
    public GomokuMove select(GomokuState state) {
      if (state.isTerminal())
        throw new TerminalPositionException();

      int i;
      ThreadLocalRandom random = ThreadLocalRandom.current();
      do {
        i = random.nextInt(boardLen);
      } while (state.board[i] != 0);

      return GomokuMove.of(i);
    }
  }

  static class RandomNeighborSelector
      implements MoveSelector<GomokuState, GomokuMove> {
    final int size;
    final int board_len;
    final List<int[]> neighbors = new ArrayList<>();

    RandomNeighborSelector(int size) {
      this.size = size;
      this.board_len = size * size;

      List<Integer> p_neighbors = new ArrayList<>();
      for (int p = 0; p < board_len; p++) {
        p_neighbors.clear();
        int x = p / size;
        int y = p % size;
        if (y > 0) {
          p_neighbors.add(p - 1);
        }
        if (y < size - 1) {
          p_neighbors.add(p + 1);
        }
        if (x > 0) {
          p_neighbors.add(p - size);
          if (y > 0) {
            p_neighbors.add(p - size - 1);
          }
          if (y < size - 1) {
            p_neighbors.add(p - size + 1);
          }
        }
        if (x < size - 1) {
          p_neighbors.add(p + size);
          if (y > 0) {
            p_neighbors.add(p + size - 1);
          }
          if (y < size - 1) {
            p_neighbors.add(p + size + 1);
          }
        }

        int[] neighbors_arr = new int[p_neighbors.size()];
        for (int i = 0; i < p_neighbors.size(); i++) {
          neighbors_arr[i] = p_neighbors.get(i);
        }

        neighbors.add(neighbors_arr);
      }
    }

    @Override
    public GomokuMove select(GomokuState state) {
      if (state.isTerminal())
        throw new TerminalPositionException();

      ThreadLocalRandom random = ThreadLocalRandom.current();

      int i;
      boolean found_neighbor = false;
      do {
        i = random.nextInt(board_len);
        if (state.board[i] != 0) {
          continue;
        }
        if (i == board_len / 2) {
          break;
        }
        found_neighbor = false;
        for (int ni : neighbors.get(i)) {
          if (state.board[ni] != 0) {
            found_neighbor = true;
            break;
          }
        }
      } while (!found_neighbor);

      return GomokuMove.of(i);
    }
  }

  GomokuState(int size, Limits limits) {
    this.size = size;
    this.limits = limits;
    board = new byte[size * size];
    status = GameStatusInt.init();
  }

  void reset() {
    Arrays.fill(board, (byte)0);
    status = GameStatusInt.init();
  }

  @Override public Gomoku getGame() {
    return Gomoku.getInstance(size);
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

  @Override public GomokuMove getRandomMove() {
    return getGame().getRandomMoveSelector().select(this);
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

  int get(int point) {
    return board[point];
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
