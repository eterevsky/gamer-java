package gamer.chess;

import static gamer.chess.Pieces.piece;

final class MutableBoard {
  private final byte[] board;

  private static final byte[] INITIAL_BOARD = Util.hexStringToByteArray(
      "020100000000090a" +
      "030100000000090b" +
      "040100000000090c" +
      "050100000000090d" +
      "060100000000090e" +
      "040100000000090c" +
      "030100000000090b" +
      "020100000000090a");

  MutableBoard() {
    board = INITIAL_BOARD;
  }

  private MutableBoard(byte[] board) {
    this.board = board;
  }

  static Board fromBytes(byte[] board) {
    return new MutableBoard(board);
  }

  byte get(int cell) {
    return board[cell];
  }

  byte get(int col, int row) {
    return board[col * 8 + row - 9];
  }

  byte getPiece(int cell) {
    return piece(get(cell));
  }

  byte getPiece(int col, int row) {
    return piece(get(col, row));
  }
}
