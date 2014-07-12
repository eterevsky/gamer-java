package gamer.gomoku;

import gamer.def.GameException;
import gamer.def.GameState;
import gamer.def.GameResult;
import gamer.def.IllegalMoveException;
import gamer.def.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public final class GomokuState implements GameState<Gomoku> {
  private final BitSet cell;
  private final BitSet cellx;
  private int movesCount = 0;
  private GameResult result;
  private boolean terminal = false;

  GomokuState() {
    cell = new BitSet(Gomoku.CELLS);
    cellx = new BitSet(Gomoku.CELLS);
  }

  private GomokuState(GomokuState other) {
    cell = (BitSet) other.cell.clone();
    cellx = (BitSet) other.cellx.clone();
    movesCount = other.movesCount;
    terminal = other.terminal;
    result = other.result;
  }

  @Override
  public GomokuState clone() {
    return new GomokuState(this);
  }

  public void play(Move<Gomoku> moveInt) {
    GomokuMove move = (GomokuMove) moveInt;

    if (isTerminal()) {
      throw new IllegalMoveException(this, move, "state is terminal");
    }

    if (cell.get(move.cell)) {
      throw new IllegalMoveException(this, move, "cell is not empty");
    }

    if (move.player != getPlayer()) {
      throw new IllegalMoveException(this, move, "wrong player");
    }

    cell.set(move.cell);
    if (move.player) {
      cellx.set(move.cell);
    }

    movesCount += 1;
    updateState(move.cell, move.player);
  }

  public boolean getPlayer() {
    return movesCount % 2 == 0;
  }

  public boolean isTerminal() {
    return terminal;
  }

  public GameResult getResult() {
    if (!isTerminal())
      throw new GameException();
    return result;
  }

  public List<Move<Gomoku>> getMoves() {
    List<Move<Gomoku>> moves = new ArrayList<>();
    for (int i = 0; i < Gomoku.CELLS; i++) {
      if (!cell.get(i)) {
        moves.add(GomokuMove.of(i, getPlayer()));
      }
    }

    return moves;
  }

  public GomokuMove getRandomMove(Random random) {
    if (isTerminal())
      throw new GameException();

    int i;
    do {
      i = random.nextInt(Gomoku.CELLS);
    } while (cell.get(i));

    return GomokuMove.of(i, getPlayer());
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append('\n');
    for (int i = 0; i < Gomoku.SIZE * Gomoku.SIZE; i++) {
      if (cell.get(i)) {
        if (cellx.get(i)) {
          builder.append('X');
        } else {
          builder.append('O');
        }
      } else {
        builder.append('.');
      }

      if (i % Gomoku.SIZE == Gomoku.SIZE - 1) {
        builder.append('\n');
      } else {
        builder.append(' ');
      }
    }

    return builder.toString();
  }

  private boolean checkLine(int from, int to, int delta, boolean player) {
    int filled = 0;
    for (int i = from; i <= to; i += delta) {
      if (cell.get(i) && cellx.get(i) == player) {
        filled += 1;
        if (filled == 5)
          return true;
      } else {
        filled = 0;
      }
    }

    return false;
  }

  private void updateState(int cell, boolean player) {
    int x = cell % Gomoku.SIZE;
    int y = cell / Gomoku.SIZE;

    int left = Math.min(x, 4);
    int right = Math.min(Gomoku.SIZE - x - 1, 4);
    int top = Math.min(y, 4);
    int bottom = Math.min(Gomoku.SIZE - y - 1, 4);

    boolean won = checkLine(cell - left, cell + right, 1, player)
               || checkLine(cell - top * Gomoku.SIZE,
                            cell + bottom * Gomoku.SIZE,
                            Gomoku.SIZE,
                            player)
               || checkLine(cell - Math.min(left, top) * (Gomoku.SIZE + 1),
                            cell + Math.min(right, bottom) * (Gomoku.SIZE + 1),
                            Gomoku.SIZE + 1,
                            player)
               || checkLine(cell - Math.min(right, top) * (Gomoku.SIZE - 1),
                            cell + Math.min(left, bottom) * (Gomoku.SIZE - 1),
                            Gomoku.SIZE - 1,
                            player);

    if (won) {
      terminal = true;
      result = player ? GameResult.WIN : GameResult.LOSS;
    } else if (movesCount == Gomoku.CELLS) {
      terminal = true;
      result = GameResult.DRAW;
    } else {
      terminal = false;
    }
  }

}
