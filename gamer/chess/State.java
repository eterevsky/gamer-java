package gamer.chess;

import gamer.def.GameStatus;

import java.util.List;

interface State {
  static final byte WHITE_SHORT_CASTLING = 1;
  static final byte WHITE_LONG_CASTLING = 2;
  static final byte BLACK_SHORT_CASTLING = 4;
  static final byte BLACK_LONG_CASTLING = 8;

  static final int MOVES_WITHOUT_CAPTURE = 100;

  Board getBoard();
  byte getCastlings();
  int getEnPassant();
  int getMovesSinceCapture();
  int getMovesCount();
  GameStatus status();
  List<ChessMove> getMoves();
}