package gomoku;

import gamer.GameException;
import gamer.GameState;
import gamer.Move;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GomokuState implements GameState<Gomoku> {
  private enum CellState {
    EMPTY,
    X,
    O
  };

  private CellState field[] = new CellState[Gomoku.SIZE * Gomoku.SIZE];
  private boolean firstPlayersTurn = true;
  private boolean terminal = false;
  private int result;

  /* package */ GomokuState() {
    Arrays.fill(field, CellState.EMPTY);
  }

  public void play(Move<Gomoku> moveInt) throws GameException {
    GomokuMove move = (GomokuMove) moveInt;

    if (isTerminal() ||
        field[move.cell] != CellState.EMPTY ||
        move.player != firstPlayersTurn) {
      System.out.println(move.player);
      System.out.println(firstPlayersTurn);
      throw new GameException("wrong move: " + move.toString());
    }

    if (move.player) {
      field[move.cell] = CellState.X;
    } else {
      field[move.cell] = CellState.O;
    }

    firstPlayersTurn = !firstPlayersTurn;

    updateState();
  }

  public boolean isFirstPlayersTurn() {
    return firstPlayersTurn;
  }

  public boolean isTerminal() {
    return terminal;
  }

  public int getResult() throws GameException {
    if (!isTerminal())
      throw new GameException();
    return result;
  }

  public List<Move<Gomoku>> getAvailableMoves() {
    List<Move<Gomoku>> moves = new ArrayList<>();
    for (int i = 0; i < field.length; i++) {
      if (field[i] == CellState.EMPTY) {
        // Move<Gomoku> move = new GomokuMove(i, firstPlayersTurn);
        moves.add(new GomokuMove(i, firstPlayersTurn));
      }
    }

    return moves;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
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

  private boolean checkLine(int start, int delta) {
    int position = start;
    for (int i = 0; i < 4; i++) {
      int next = position + delta;
      if (field[position] != field[next])
        return false;
      position = next;
    }
    return true;
  }

  private void updateState() {
    boolean foundEmpty = false;
    terminal = false;

    for (int i = 0; i < field.length; i++) {
      CellState cell = field[i];
      if (cell == CellState.EMPTY) {
        foundEmpty = true;
        continue;
      }

      int x = i % Gomoku.SIZE;
      int y = i / Gomoku.SIZE;

      boolean won = false;
      if (x <= Gomoku.SIZE - 5)
        won = won || checkLine(i, 1);

      if (y <= Gomoku.SIZE - 5)
        won = won || checkLine(i, Gomoku.SIZE);

      if (x <= Gomoku.SIZE - 5 && y <= Gomoku.SIZE - 5)
        won = won || checkLine(i, Gomoku.SIZE + 1);

      if (x >= 4 && y <= Gomoku.SIZE - 5)
        won = won || checkLine(i, Gomoku.SIZE - 1);

      if (won) {
        terminal = true;
        result = (cell == CellState.X) ? 1 : -1;
        return;
      }
    }

    if (!foundEmpty) {
      terminal = true;
      result = 0;
    }
  }
}
