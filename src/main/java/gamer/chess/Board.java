package gamer.chess;

public final class Board implements Cloneable {
  byte[] board = new byte[64];

  Board() {}

  static int a2i(String a) {
    assert a.length() == 2;
    return (a.charAt(0) - 'a') * 8 + a.charAt(1) - '1';
  }

  static int cr2i(int col, int row) {
    return col * 8 + row - 9;
  }

  static String i2a(int idx) {
    return String.format("%c%c", 'a' + idx / 8, '1' + idx % 8);
  }

  static int i2row(int idx) {
    return idx % 8 + 1;
  }

  static int i2col(int idx) {
    return idx / 8 + 1;
  }

  static String i2cola(int idx) {
    return String.format("%c", 'a' + idx / 8);
  }

  @Override public Board clone() {
    try {
      Board result = (Board) super.clone();
      result.board = board.clone();
      return result;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError(e);
    }
  }

  byte get(int square) {
    return board[square];
  }

  byte get(int col, int row) {
    return board[Board.cr2i(col, row)];
  }

  byte get(String square) {
    return board[Board.a2i(square)];
  }

  byte getPiece(int square) {
    return Pieces.piece(get(square));
  }

  byte getPiece(int col, int row) {
    return getPiece(Board.cr2i(col, row));
  }

  void set(int square, byte value) {
    board[square] = value;
  }

  void move(int from, int to) {
    board[to] = board[from];
    board[from] = Pieces.EMPTY;
  }

  public boolean isEmpty(int square) {
    return board[square] == Pieces.EMPTY;
  }

  boolean isEmpty(int col, int row) {
    return isEmpty(Board.cr2i(col, row));
  }

  boolean isEmpty(String square) {
    return isEmpty(a2i(square));
  }

  boolean isWhite(int square) {
    return !isEmpty(square) && Pieces.isWhite(board[square]);
  }

  boolean isWhite(int col, int row) {
    return isWhite(cr2i(col, row));
  }

  boolean isBlack(int square) {
    return Pieces.isBlack(board[square]);
  }

  boolean color(int square) {
    return Pieces.color(board[square]);
  }

  boolean color(int col, int row) {
    return color(Board.cr2i(col, row));
  }
}
