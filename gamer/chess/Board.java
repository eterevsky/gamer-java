package gamer.chess;

import static gamer.chess.Pieces.piece;

final class Board {
  private final byte[] board;

  private Board(byte[] board) {
    this.board = board;
  }

  static Board fromBytes(byte[] board) {
    return new Board(board.clone());
  }

  static Board ownBytes(byte[] board) {
    return new Board(board);
  }


  MutableBoard mutableClone() {
    return MutableBoard.fromBytes(board.clone());
  }


  byte get(int cell) {
    return board[cell];
  }

  byte get(int col, int row) {
    return board[cr2i(col, row)];
  }

  byte get(String cell) {
    return board[a2i(cell)];
  }

  byte getPiece(int cell) {
    return piece(get(cell));
  }

  byte getPiece(int col, int row) {
    return piece(get(col, row));
  }

  byte getPiece(String cell) {
    return piece(get(cell));
  }

  boolean isEmpty(int cell) {
    return board[cell] == Pieces.EMPTY;
  }


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
}
