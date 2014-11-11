package gamer.gomoku;

import static gamer.gomoku.Gomoku.POINTS;
import static gamer.gomoku.Gomoku.SIZE;
import static java.lang.Math.max;
import static java.lang.Math.min;

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
  private final GameStatus status;

  public GameStatus status() {
    return status;
  }

  public GomokuState play(Move<Gomoku> moveInt) {
    GomokuMove move = (GomokuMove) moveInt;

    if (status.isTerminal()) {
      throw new IllegalMoveException(this, move, "state is terminal");
    }

    if (marked.get(move.point)) {
      throw new IllegalMoveException(this, move, "cell is not empty");
    }

    return new GomokuState(this, move);
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

  GomokuState() {
    marked = new BitSet(POINTS);
    markedx = new BitSet(POINTS);
    status = GameStatus.FIRST_PLAYER;
  }

  private GomokuState(GomokuState other, GomokuMove move) {
    marked = (BitSet) other.marked.clone();
    marked.set(move.point);
    if (other.status().getPlayer()) {
      markedx = (BitSet) other.markedx.clone();
      markedx.set(move.point);
    } else {
      markedx = other.markedx;
    }
    status = updateStatus(other.status().getPlayer(), move);
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
