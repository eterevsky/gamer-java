package gamer.chess;

final class Board {
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

  private byte[] board = new byte[64];

  Board() {}

  Board(byte[] board) {
    this.board = board;
  }

  byte[] toBytes() {
    return board.clone();
  }

  byte[] toBytesDisown() {
    byte[] temp = board;
    board = null;
    return temp;
  }

  byte get(int cell) {
    return board[cell];
  }

  byte get(int col, int row) {
    return board[Board.cr2i(col, row)];
  }

  byte get(String cell) {
    return board[Board.a2i(cell)];
  }

  byte getPiece(int cell) {
    return Pieces.piece(get(cell));
  }

  byte getPiece(int col, int row) {
    return getPiece(Board.cr2i(col, row));
  }

  void set(int cell, byte value) {
    board[cell] = value;
  }

  void move(int from, int to) {
    board[to] = board[from];
    board[from] = Pieces.EMPTY;
  }

  boolean isEmpty(int cell) {
    return board[cell] == Pieces.EMPTY;
  }

  boolean isEmpty(int col, int row) {
    return isEmpty(Board.cr2i(col, row));
  }

  boolean isEmpty(String cell) {
    return isEmpty(a2i(cell));
  }

  boolean isWhite(int cell) {
    return !isEmpty(cell) && Pieces.isWhite(board[cell]);
  }

  boolean isWhite(int col, int row) {
    return isWhite(cr2i(col, row));
  }

  boolean isBlack(int cell) {
    return Pieces.isBlack(board[cell]);
  }

  boolean color(int cell) {
    return Pieces.color(board[cell]);
  }

  boolean color(int col, int row) {
    return color(Board.cr2i(col, row));
  }
}
