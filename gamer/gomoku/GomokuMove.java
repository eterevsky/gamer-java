package gamer.gomoku;

import gamer.def.Move;

import java.util.ArrayList;
import java.util.List;

public final class GomokuMove implements Move<Gomoku> {
  final int cell;
  final boolean player;

  private static final List<GomokuMove> player1Moves, player2Moves;

  static {
    player1Moves = new ArrayList<>(Gomoku.CELLS);
    player2Moves = new ArrayList<>(Gomoku.CELLS);
    for (int cell = 0; cell < Gomoku.CELLS; cell++) {
      player1Moves.add(new GomokuMove(cell, true));
      player2Moves.add(new GomokuMove(cell, false));
    }
  }

  static GomokuMove of(int cell, boolean player) {
    if (player) {
      return player1Moves.get(cell);
    } else {
      return player2Moves.get(cell);
    }
  }

  static GomokuMove create(char player, int x, int y) {
    boolean playerBool;
    switch (player) {
      case 'X': playerBool = true; break;
      case 'O': playerBool = false; break;
      default: throw new RuntimeException();
    }
    return GomokuMove.of(y * Gomoku.SIZE + x, playerBool);
  }

  private GomokuMove(int cell, boolean player) {
    this.cell = cell;
    this.player = player;
  }

  @Override
  public String toString() {
    return String.format("%s (%d, %d)",
                         (this.player ? "X" : "0"),
                         cell / Gomoku.SIZE,
                         cell % Gomoku.SIZE);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof GomokuMove))
      return false;
    GomokuMove oMove = (GomokuMove) o;
    return cell == oMove.cell && player == oMove.player;
  }

  @Override
  public int hashCode() {
    return (player ? Gomoku.CELLS : 0) + cell;
  }
}
