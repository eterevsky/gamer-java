package gamer.gomoku;

import gamer.def.Move;

import java.util.ArrayList;
import java.util.List;

public final class GomokuMove implements Move {
  final int point;

  private static final List<GomokuMove> instances = new ArrayList<>();
  private static final String COL_LETTER = "abcdefghjklmnopqrstuvwxyz";

  private GomokuMove(int point) {
    this.point = point;
  }

  static void createInstances(int size) {
    for (int point = instances.size(); point < size * size; point++) {
      instances.add(new GomokuMove(point));
    }
  }

  static GomokuMove of(int point) {
    return instances.get(point);
  }

  static GomokuMove of(int col, int row, int size) {
    if (col < 0 || col >= size || row < 0 || row > size) {
      throw new RuntimeException(
          String.format("Wrong coodinates: %d %d", col, row));
    }
    return GomokuMove.of(col * size + row);
  }
	
	static GomokuMove of(int col, int row) {
		return of(col, row, Gomoku.DEFAULT_SIZE);
	}

  static GomokuMove of(String moveStr, int size) {
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

    return of(col, row - 1, size);
  }
	
  static GomokuMove of(String moveStr) {
	  return of(moveStr, Gomoku.DEFAULT_SIZE);
  }
	
	static {
		createInstances(19);
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
