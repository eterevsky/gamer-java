package gamer.chess;

import static gamer.chess.Pieces.EMPTY;
import static gamer.chess.Pieces.PAWN;
import static gamer.chess.Pieces.ROOK;
import static gamer.chess.Pieces.KNIGHT;
import static gamer.chess.Pieces.BISHOP;
import static gamer.chess.Pieces.QUEEN;
import static gamer.chess.Pieces.KING;
import static gamer.chess.Pieces.WHITE;
import static gamer.chess.Pieces.BLACK;

import gamer.def.Helper;
import gamer.def.GameState;
import gamer.def.GameStatus;

public class ChessEndingHelper implements Helper<Chess> {
  public Helper.Result evaluate(GameState<Chess> stateI) {
    return null;
  }
}
