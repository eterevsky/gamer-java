package gamer.chess;

class StateBuilder {
  private static final byte WHITE_SHORT_CASTLING = 1;
  private static final byte WHITE_LONG_CASTLING = 2;
  private static final byte BLACK_SHORT_CASTLING = 4;
  private static final byte BLACK_LONG_CASTLING = 8;

  private static final int MOVES_WITHOUT_CAPTURE = 150;

  private Board board;
  private boolean player = true;
  private byte castlings = WHITE_LONG_CASTLING | WHITE_SHORT_CASTLING |
                           BLACK_LONG_CASTLING | BLACK_SHORT_CASTLING;

  private int enPassant = -1;  // -1 if no en passant pawn,
                               // otherwise the passed empty square
  private int movesSinceCapture = 0;
  private int movesCount = 0;

  StateBuilder(Board board) {
    this.board = board;
  }

  Board getBoard() {
    return board;
  }

  byte getCastlings() {
    return castlings;
  }

  int getEnPassant() {
    return enPassant;
  }

  int getMovesSinceCapture() {
    return movesSinceCapture;
  }

  int getMovesCount() {
    return movesCount;
  }
}