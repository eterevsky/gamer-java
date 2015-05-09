package gamer.chess;

import gamer.def.Position;

interface State<S extends State<S>> extends Position<S, ChessMove> {
  byte WHITE_SHORT_CASTLING = 1;
  byte WHITE_LONG_CASTLING = 2;
  byte BLACK_SHORT_CASTLING = 4;
  byte BLACK_LONG_CASTLING = 8;

  int MOVES_WITHOUT_CAPTURE = 100;

  /**
   * @return The board. Not the copy, so the result shouldn't be edited.
   */
  Board getBoard();
  byte getCastlings();
  int getEnPassant();
  int getMovesSinceCapture();
  int getMovesCount();
}
