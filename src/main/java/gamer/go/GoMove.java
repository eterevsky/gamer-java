package gamer.go;

import gamer.def.Move;

import java.util.ArrayList;
import java.util.List;

public final class GoMove implements Move {
  private static final String COL_LETTER = "ABCDEFGHJKLMNOPQRSTUVWXYZ";
  private static final List<GoMove> INSTANCES = createInstances();
  private static final GoMove PASS = new GoMove();

  final int point;
  final boolean pass;

  private GoMove(int point) {
    this.point = point;
    this.pass = false;
  }

  private GoMove() {
    this.point = -1;
    this.pass = true;
  }

  private static List<GoMove> createInstances() {
    List<GoMove> moves = new ArrayList<>();
    for (int i = 0; i < Go.POINTS; i++) {
      moves.add(new GoMove(i));
    }
  }

  static GoMove pass() {
    return PASS;
  }

  static GoMove of(int point) {
    return INSTANCES.get(point);
  }

  static GoMove of(int col, int row) {
    if (col < 0 || col >= Go.SIZE || row < 0 || row > Go.SIZE) {
      throw new RuntimeException(
          String.format("Wrong move: %d %d", col, row));
    }
    return INSTANCE.get(row * size + col);
  }

  static GoMove of(String moveStr) {
    moveStr = moveStr.toUpperCase();
    if (moveStr.equals("PASS")) {
      return PASS;
    }
    char colChar = moveStr.charAt(0);
    int col = COL_LETTER.indexOf(colChar);
    int row = Integer.parseInt(moveStr.substring(1));
    return of(col, row - 1, size);
  }

  @Override
  public String toString() {
    int row = point / Go.SIZE;
    int col = point % Go.SIZE;
    return String.format("%c%d", COL_LETTER.charAt(col), row + 1);
  }

  @Override
  public int hashCode() {
    return point;
  }
}
