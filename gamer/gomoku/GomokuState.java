package gamer.gomoku;

import static gamer.gomoku.Gomoku.CELLS;
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

    if (marked.get(move.cell)) {
      throw new IllegalMoveException(this, move, "cell is not empty");
    }

    if (move.player != status.getPlayer()) {
      throw new IllegalMoveException(this, move, "wrong player");
    }

    return new GomokuState(this, move);
  }

  public boolean isTerminal() {
    return status.isTerminal();
  }

  public List<Move<Gomoku>> getMoves() {
    List<Move<Gomoku>> moves = new ArrayList<>();
    for (int i = 0; i < CELLS; i++) {
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
      i = random.nextInt(CELLS);
    } while (marked.get(i));

    return GomokuMove.of(i, status.getPlayer());
  }

  GomokuState() {
    marked = new BitSet(CELLS);
    markedx = new BitSet(CELLS);
    status = GameStatus.FIRST_PLAYER;
  }

  private GomokuState(GomokuState other, GomokuMove move) {
    marked = (BitSet) other.marked.clone();
    marked.set(move.cell);
    if (move.player) {
      markedx = (BitSet) other.markedx.clone();
      markedx.set(move.cell);
    } else {
      markedx = other.markedx;
    }
    status = updateStatus(move);
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < CELLS; i++) {
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

    int cell = center;
    int i;
    for (i = 0; i < left; i++) {
      cell -= delta;
      if (!marked.get(cell) || markedx.get(cell) != player)
        break;
    }
    int leftTail = i;

    cell = center;
    for (i = 0; i < right; i++) {
      cell += delta;
      if (!marked.get(cell) || markedx.get(cell) != player)
        break;
    }
    int rightTail = i;

    return leftTail + rightTail >= 4;
  }

  private GameStatus updateStatus(GomokuMove move) {
    int cell = move.cell;
    int x = cell % SIZE;
    int y = cell / SIZE;

    int left = min(x, 4);
    int right = min(SIZE - x - 1, 4);
    int top = min(y, 4);
    int bottom = min(SIZE - y - 1, 4);

    boolean won =
        checkLine(cell, left, right, 1) ||
        checkLine(cell, top, bottom, SIZE) ||
        checkLine(cell, min(left, top), min(right, bottom), SIZE + 1) ||
        checkLine(cell, min(right, top), min(left, bottom), SIZE - 1);

    if (won) {
      return move.player ? GameStatus.WIN : GameStatus.LOSS;
    } else if (marked.nextClearBit(0) == CELLS) {
      return GameStatus.DRAW;
    } else {
      return move.player ? GameStatus.SECOND_PLAYER : GameStatus.FIRST_PLAYER;
    }
  }

  public String moveToString(Move<Gomoku> move, boolean showMoveNumber) {
    String moveNumber = "";
    if (showMoveNumber) {
      int nonEmpty = 0;
      for (int i = 0; i < CELLS; i++) {
        if (marked.get(i))
          nonEmpty++;
      }

      moveNumber = String.format("%d. ", nonEmpty / 2 + 1);
      if (status == GameStatus.SECOND_PLAYER)
        moveNumber += "... ";
    }

    return moveNumber + move.toString();
  }

}
