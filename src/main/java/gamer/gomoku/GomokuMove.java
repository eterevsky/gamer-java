package gamer.gomoku;

import gamer.def.Move;

import java.util.ArrayList;
import java.util.List;

public final class GomokuMove implements Move {
  final int point;

  static GomokuMove of(int point) {
    return instances.get(point);
  }

  public static GomokuMove of(int col, int row) {
    if (col < 0 || col >= Gomoku.SIZE || row < 0 || row > Gomoku.SIZE) {
      throw new RuntimeException(
          String.format("Wrong coodinates: %d %d", col, row));
    }
    return GomokuMove.of(col * Gomoku.SIZE + row);
  }

  public static GomokuMove of(String moveStr) {
    char colChar = moveStr.charAt(0);
    int col;
    if ('a' <= colChar && colChar <= 'h') {
      col = colChar - 'a';
    } else if ('j' <= colChar && colChar <= 'z') {
      col = colChar - 'a' - 1;
    } else if ('A' <= colChar && colChar <= 'H') {
      col = colChar - 'A';
    } else if ('J' <= colChar && colChar <= 'Z') {
      col = colChar - 'A' - 1;
    } else {
      throw new RuntimeException("Can't parse: " + moveStr);
    }

    int row = 0;
    for (int i = 1; i < moveStr.length(); i++) {
      char c = moveStr.charAt(i);
      if (c < '0' || c > '9') {
        throw new RuntimeException("Can't parse: " + moveStr);
      }

      row = 10 * row + (c - '0');
    }

    return of(col, row - 1);
  }

  @Override
  public String toString() {
    int col = point % Gomoku.SIZE;
    int row = point % Gomoku.SIZE + 1;
    return String.format("%c%d", COL_LETTER.charAt(col), row);
  }

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
