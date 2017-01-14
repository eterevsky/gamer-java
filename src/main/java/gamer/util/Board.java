package gamer.util;

import java.util.List;

/**
 * Utilities for games, that contain rectangular board.
 *
 * The tiles are numbered as follows:
 *
 * 3 | 6 7 8
 * 2 | 3 4 5
 * 1 | 0 1 2
 *   +------
 *     A B C
 */
public class Board {
  private static final String COL_LETTER = "ABCDEFGHJKLMNOPQRSTUVWXYZ";

  private final int width;
  private final int height;
  private final List<String> tileContents;

  public Board(int width, int height, List<String> tileContents) {
    if (width > 25) {
      throw new IllegalArgumentException(
          "util.Board doesn't support width >25");
    }
    this.width = width;
    this.height = height;
    this.tileContents = tileContents;
  }

  public int right() {
    return 1;
  }

  public int left() {
    return -1;
  }

  public int up() {
    return width;
  }

  public int down() {
    return -width;
  }

  public int tile(int x, int y) {
    return y * width + x;
  }

  public int x(int tile) {
    return tile % width;
  }

  public int y(int tile) {
    return tile / width;
  }

  public String tileToString(int tile) {
    return String.format("%c%d", COL_LETTER.charAt(x(tile)), y(tile) + 1);
  }

  public int parseTile(String tileStr) {
    tileStr = tileStr.toUpperCase();
    char colChar = tileStr.charAt(0);
    int x = COL_LETTER.indexOf(colChar);
    int y = Integer.parseInt(tileStr.substring(1));
    return tile(x, y);
  }

  public String boardToString(byte[] board, boolean printCoordinates) {
    int tileLen = 1;
    for (String s : tileContents) {
      if (s.length() > tileLen) {
        tileLen = s.length();
      }
    }

    tileLen += 1;
    StringBuilder builder;

    for (int y = height - 1; y >= 0; y--) {

    }
  }
}
