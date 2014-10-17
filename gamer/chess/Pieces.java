package gamer.chess;

class Pieces {
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

  private static final char PIECE_NAMES[] = {
      '.', 'P', 'R', 'N', 'B', 'Q', 'K', '?', '?', 'p', 'r', 'n', 'b', 'q', 'k'
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
}
