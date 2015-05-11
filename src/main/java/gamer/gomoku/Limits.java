package gamer.gomoku;

import static java.lang.Math.min;

/**
 * For each point on the board -- the end points of horizontal, vertical and
 * diagonal lines of the length up to 5.
 */
final class Limits {
  final int[] n, e, s, w;
  final int[] ne, se, sw, nw;
  final int size;

  Limits(int size) {
    this.size = size;
    int points = size * size;
    n = new int[points];
    e = new int[points];
    s = new int[points];
    w = new int[points];
    ne = new int[points];
    se = new int[points];
    sw = new int[points];
    nw = new int[points];

    fillTables();
  }

  private void fillTables() {
    for (int point = 0; point < size * size; point++) {
      int x = point % size;
      int y = point / size;

      int left = min(x, 4);
      int right = min(size - x - 1, 4);
      int top = min(y, 4);
      int bottom = min(size - y - 1, 4);

      n[point] = point - size * top;
      e[point] = point + right;
      s[point] = point + size * bottom;
      w[point] = point - left;

      ne[point] = point - (size - 1) * min(right, top);
      se[point] = point + (size + 1) * min(right, bottom);
      sw[point] = point + (size - 1) * min(left, bottom);
      nw[point] = point - (size + 1) * min(left, top);
    }
  }
}