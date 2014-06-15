package gomoku;

import gamer.Move;

public class GomokuMove implements Move<Gomoku> {
  /* package */ int cell;
  /* package */ boolean player;

  /* package */ GomokuMove(int cell, boolean player) {
    this.cell = cell;
    this.player = player;
  }

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
    return (player ? Gomoku.SIZE : 0) + cell;
  }
}
