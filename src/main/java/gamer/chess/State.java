package gamer.chess;

import gamer.def.Position;

interface State<S extends State<S>> extends Position<S, ChessMove> {
  byte WHITE_SHORT_CASTLING = 1;
  byte WHITE_LONG_CASTLING = 2;
  byte BLACK_SHORT_CASTLING = 4;
  byte BLACK_LONG_CASTLING = 8;

  int MOVES_WITHOUT_CAPTURE = 100;

  byte get(int square);
  byte get(String square);
  /** Zero-based. */
  byte get(int col, int row);
  byte getCastlings();
  int getEnPassant();
  int getMovesSinceCapture();
  int getMovesCount();
}
