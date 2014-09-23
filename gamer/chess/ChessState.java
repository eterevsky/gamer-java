package gamer.chess;

import static Chess.CELLS;
import static Chess.SIZE;

import gamer.def.GameState;

public final class ChessState implements GameState<Chess> {

  // 0 - empty
  // 1 - pawn
  // 2 - rook
  // 3 - knight
  // 4 - bishop
  // 5 - queen
  // 6 - king
  // & 8 - white
  // & 16 - pawn en passant
  // & 32 - rook with aloud castle
  private final byte[CELLS] board;
}
