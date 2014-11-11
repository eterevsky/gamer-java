package gamer.gomoku;

import gamer.def.Move;

import java.util.ArrayList;
import java.util.List;

public final class GomokuMove implements Move<Gomoku> {
  final int point;

  static GomokuMove of(int point) {
    return instances.get(point);
  }

  public static GomokuMove of(int col, int row) {
    return GomokuMove.of(col * Gomoku.SIZE + row);
  }

  @Override
  public String toString() {
    int col = point % Gomoku.SIZE;
    int row = point % Gomoku.SIZE + 1;
    return String.format("%c%d", COL_LETTER.charAt(col), row);
  }

  // @Override
  // public boolean equals(Object o) {
  //   if (!(o instanceof GomokuMove))
  //     return false;
  //   GomokuMove oMove = (GomokuMove) o;
  //   return point == oMove.point;
  // }
  //
  @Override
  public int hashCode() {
    return point;
  }

  private static final List<GomokuMove> instances = genInstances();
  private static final String COL_LETTER = "abcdefghjklmnopqrstuvwxyz";

  private GomokuMove(int point) {
    this.point = point;
  }

  private static List<GomokuMove> genInstances() {
    List<GomokuMove> inst = new ArrayList<>(Gomoku.POINTS);
    for (int point = 0; point < Gomoku.POINTS; point++) {
      inst.add(new GomokuMove(point));
    }
    return inst;
  }
}
