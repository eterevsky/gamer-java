package gamer.chess;

import static gamer.chess.Pieces.piece;

final class MutableBoard {
  private byte[] board;

  static final MutableBoard INITIAL_BOARD =
      fromBytes(Util.hexStringToByteArray(
          "020100000000090a" +
          "030100000000090b" +
          "040100000000090c" +
          "050100000000090d" +
          "060100000000090e" +
          "040100000000090c" +
          "030100000000090b" +
          "020100000000090a"));

  private MutableBoard(byte[] board) {
    this.board = board;
  }

  static MutableBoard fromBytes(byte[] board) {
    return new MutableBoard(board);
  }

  byte[] toBytes() {
    return board;
  }

  byte get(int cell) {
    return board[cell];
  }

  byte get(String cell) {
    return board[Board.a2i(cell)];
  }

  byte getPiece(int cell) {
    return piece(get(cell));
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

  boolean isWhite(int cell) {
    return Pieces.isWhite(board[cell]);
  }

  boolean isBlack(int cell) {
    return Pieces.isBlack(board[cell]);
  }

  boolean color(int cell) {
    return Pieces.color(board[cell]);
  }

  Board toBoard() {
    byte[] temp = board;
    board = null;
    return Board.ownBytes(board);
  }
}