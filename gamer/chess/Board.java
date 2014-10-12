package gamer.chess;

class Board{
  static final int
      A1 = 0, A8 = 7,
      C1 = 16, C8 = 23,
      D1 = 24, D8 = 31,
      E1 = 32, E8 = 39,
      F1 = 40, F8 = 47,
      G1 = 48, G8 = 55,
      H1 = 56, H8 = 63;

  static byte get(byte[] board, String cell) {
    return board[a2i(cell)];
  }

  static int a2i(String a) {
    assert a.length() == 2;
    return (a.charAt(0) - 'a') * 8 + a.charAt(1) - '1';
  }

  static int cr2i(int col, int row) {
    return col * 8 + row - 9;
  }

  static String i2a(int idx) {
    return "" + ('a' + idx / 8) + ('0' + idx % 8);
  }

  static int i2row(int idx) {
    return idx % 8 + 1;
  }

  static int i2col(int idx) {
    return idx / 8 + 1;
  }
}
