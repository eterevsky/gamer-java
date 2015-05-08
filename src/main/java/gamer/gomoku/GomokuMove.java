package gamer.gomoku;

import gamer.def.Move;

import java.util.ArrayList;
import java.util.List;

public final class GomokuMove implements Move {
  private static final List<GomokuMove> INSTANCES = new ArrayList<>();
  private static final String COL_LETTER = "abcdefghjklmnopqrstuvwxyz";
  final int point;

  private GomokuMove(int point) {
    this.point = point;
  }

  static void createInstances(int size) {
    for (int point = INSTANCES.size(); point < size * size; point++) {
      INSTANCES.add(new GomokuMove(point));
    }
  }

  static GomokuMove of(int point) {
    return INSTANCES.get(point);
  }

  static GomokuMove of(int col, int row, int size) {
    if (col < 0 || col >= size || row < 0 || row > size) {
      throw new RuntimeException(
          String.format("Wrong move: %d %d (size: %d)", col, row, size));
    }
    return GomokuMove.of(row * size + col);
  }
	
  static GomokuMove of(String moveStr, int size) {
    moveStr = moveStr.toLowerCase();
    char colChar = moveStr.charAt(0);
    int col = COL_LETTER.indexOf(colChar);
    int row = Integer.parseInt(moveStr.substring(1));
    return of(col, row - 1, size);
  }
	
  String toString(int size) {
    int col = point / size;
    int row = point % size;
    return String.format("%c%d", COL_LETTER.charAt(col), row + 1);
  }

  @Override
  public int hashCode() {
    return point;
  }
}
