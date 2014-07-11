package gamer.gomoku;

import gamer.def.GameException;
import gamer.def.GameState;
import gamer.def.GameResult;
import gamer.def.IllegalMoveException;
import gamer.def.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GomokuState implements GameState<Gomoku> {
  private static enum CellState {
    EMPTY,
    X,
    O
  };

  private CellState field[] = new CellState[Gomoku.SIZE * Gomoku.SIZE];
  private int movesCount = 0;
  private GameResult result;
  private boolean terminal = false;

  GomokuState() {
    Arrays.fill(field, CellState.EMPTY);
  }

  private GomokuState(GomokuState other) {
    field = other.field.clone();
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

    if (field[move.cell] != CellState.EMPTY) {
      throw new IllegalMoveException(this, move, "cell is not empty");
    }

    if (move.player != getPlayer()) {
      throw new IllegalMoveException(this, move, "wrong player");
    }

    if (move.player) {
      field[move.cell] = CellState.X;
    } else {
      field[move.cell] = CellState.O;
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

  public GameResult getResult() throws GameException {
    if (!isTerminal())
      throw new GameException();
    return result;
  }

  public List<Move<Gomoku>> getMoves() {
    List<Move<Gomoku>> moves = new ArrayList<>();
    for (int i = 0; i < field.length; i++) {
      if (field[i] == CellState.EMPTY) {
        // Move<Gomoku> move = new GomokuMove(i, firstPlayersTurn);
        moves.add(new GomokuMove(i, getPlayer()));
      }
    }

    return moves;
  }

  public GomokuMove getRandomMove() {
    if (isTerminal())
      return null;

    int cell;
    do {
      cell = ThreadLocalRandom.current().nextInt(field.length);
    } while (field[cell] != CellState.EMPTY);

    return new GomokuMove(cell, getPlayer());
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append('\n');
    for (int i = 0; i < field.length; i++) {
      switch (field[i]) {
        case EMPTY:
          builder.append('.');
          break;

        case X:
          builder.append('X');
          break;

        case O:
          builder.append('O');
          break;
      }

      if (i % Gomoku.SIZE == Gomoku.SIZE - 1) {
        builder.append('\n');
      } else {
        builder.append(' ');
      }
    }

    return builder.toString();
  }

  private boolean checkLine(int from, int to, int delta, CellState cs) {
    int filled = 0;
    for (int i = from; i <= to; i += delta) {
      if (field[i] == cs) {
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

    CellState cs = player ? CellState.X : CellState.O;

    boolean won = checkLine(cell - left, cell + right, 1, cs)
               || checkLine(cell - top * Gomoku.SIZE,
                            cell + bottom * Gomoku.SIZE,
                            Gomoku.SIZE,
                            cs)
               || checkLine(cell - Math.min(left, top) * (Gomoku.SIZE + 1),
                            cell + Math.min(right, bottom) * (Gomoku.SIZE + 1),
                            Gomoku.SIZE + 1,
                            cs)
               || checkLine(cell - Math.min(right, top) * (Gomoku.SIZE - 1),
                            cell + Math.min(left, bottom) * (Gomoku.SIZE - 1),
                            Gomoku.SIZE - 1,
                            cs);

    if (won) {
      terminal = true;
      result = player ? GameResult.WIN : GameResult.LOSS;
    } else if (movesCount == field.length) {
      terminal = true;
      result = GameResult.DRAW;
    } else {
      terminal = false;
    }
  }

}
