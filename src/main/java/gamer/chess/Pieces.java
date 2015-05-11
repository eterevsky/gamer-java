package gamer.chess;

public final class Pieces {
  static final byte PIECE_MASK = 7;

  static final byte EMPTY = 0;
  static final byte PAWN = 1;
  static final byte ROOK = 2;
  static final byte KNIGHT = 3;
  static final byte BISHOP = 4;
  static final byte QUEEN = 5;
  static final byte KING = 6;

  static final byte WHITE = 0;
  static final byte BLACK = 8;

  static final byte MAX = 14;
  static final char PIECE_LETTER[] = {
    '?', 'p', 'R', 'N', 'B', 'Q', 'K'
  };
  private static final char PIECE_NAMES[] = {
      '\u00B7', '\u2659', '\u2656', '\u2658', '\u2657', '\u2655', '\u2654', '?',
      '?', '\u265F', '\u265C', '\u265E', '\u265D', '\u265B', '\u265A'
    };

  static byte a2piece(String s) {
    if (s.length() != 1) {
      throw new RuntimeException("Unknown piece");
    }

    for (byte i = 0; i < PIECE_NAMES.length; i++) {
      if (s.charAt(i) == PIECE_NAMES[i])
        return i;
    }

    throw new RuntimeException("Unknown piece");
  }

  static char piece2a(byte p) {
    return PIECE_NAMES[p];
  }

  static boolean color(byte p) {
    return (p & BLACK) == 0;
  }

  public static boolean isEmpty(byte p) {
    return p == EMPTY;
  }

  static boolean isWhite(byte p) {
    return p != EMPTY && ((p & BLACK) == 0);
  }

  static boolean isBlack(byte p) {
    return (p & BLACK) != 0;
  }

  static byte piece(byte p) {
    return (byte) (p & PIECE_MASK);
  }

  static byte white(byte p) {
    return p;
  }

  static byte black(byte p) {
    return (byte) (p | BLACK);
  }

  static byte withColor(byte p, boolean player) {
    return (byte) (player ? p & PIECE_MASK : p | BLACK);
  }
}
