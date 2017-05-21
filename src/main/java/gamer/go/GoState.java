package gamer.go;

import gamer.def.Game;
import gamer.def.State;
import gamer.def.MoveSelector;
import gamer.def.TerminalPositionException;
import gamer.util.GameStatusInt;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;


public final class GoState implements State<GoState, GoMove> {
  byte[] board;
  private int koPoint;
  private int status;

  static class RandomSelector implements MoveSelector<GoState, GoMove> {
    @Override
    public GoMove select(GoState state) {
      return state.getRandomMove();
    }
  }

  GoState() {
    board = new byte[Go.POINTS];
    status = GameStatusInt.init();
  }

  @Override
  public Go getGame() {
    return Go.getInstance();
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
  public boolean isRandom() {
    return false;
  }

  @Override
  public int getPayoff(int player) {
    return GameStatusInt.getPayoff(status, player);
  }

  @Override
  public List<GoMove> getMoves() {
    List<GoMove> moves = new ArrayList<>();
    moves.add(GoMove.pass());
    for (int i = 0; i < Go.POINTS; i++) {
      if (isValidMove(i)) {
        moves.add(GoMove.of(i));
      }
    }
    return moves;
  }

  @Override
  public GoMove getRandomMove() {
    if (isTerminal())
      throw new TerminalPositionException();

    ThreadLocalRandom random = ThreadLocalRandom.current();
    while (true) {
      int i = random.nextInt(Go.POINTS + 1) - 1;
      if (i < 0) {
        return GoMove.pass();
      }
      if (isValidMove(i)) {
        return GoMove.of(i);
      }
    }
  }

  @Override
  public void play(GoMove move) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public GoMove parseMove(String moveStr) {
    return GoMove.of(moveStr);
  }

  @Override
  public GoState clone() {
    try {
      GoState result = (GoState) super.clone();
      result.board = board.clone();
      return result;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError(e);
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < board.length; i++) {
      if (board[i] != 0) {
        builder.append((board[i] == 1) ? 'X' : 'O');
      } else {
        builder.append((i == koPoint) ? '-' : '.');
      }

      builder.append(i % Go.SIZE == Go.SIZE - 1 ? '\n' : ' ');
    }

    return builder.toString();
  }

  private boolean isValidMove(int point) {
    if (board[point] != 0 || point == koPoint) {
      return false;
    }
    int color = getPlayerBool() ? 1 : 2;
    int otherColor = 3 - color;
    board[point] = (byte)color;
    boolean alive = isAlive(point);
    if (alive) {
      board[point] = 0;
      return true;
    }
    for (int j : Go.NEIGHBORS[point]) {
      if (board[j] == otherColor && !isAlive(j)) {
        board[point] = 0;
        return true;
      }
    }
    board[point] = 0;
    return false;
  }

  private boolean isAlive(int point) {
    for (int j : Go.NEIGHBORS[point]) {
      if (board[j] == 0) {
        return true;
      }
    }

    byte color = board[point];

    boolean[] visited = new boolean[Go.POINTS];
    Queue<Integer> queue = new ArrayDeque<>();
    queue.add(point);

    while (queue.peek() != null) {
      int p = queue.remove();
      if (visited[p]) continue;
      visited[p] = true;
      for (int n : Go.NEIGHBORS[p]) {
        if (board[n] == 0) {
          return true;
        }
        if (board[n] == color) {
          queue.add(n);
        }
      }
    }

    return false;
  }
}
