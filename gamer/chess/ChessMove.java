package gamer.chess;

import static gamer.chess.Chess.CELLS;
import static gamer.chess.Chess.SIZE;

import java.util.ArrayList;
import java.util.List;

public final class ChessMove implements Move<Chess> {
  private static String[] 
  PIECE_LETTER = {"", null, "R", "N", "B", "Q", "K"};

  final int from;
  final int to;
  final byte promote;

  private static final List<ChessMove> moves;
  private static final List<ChessMove> promotions;

  static {
    moves = new ArrayList<>(CELLS * CELLS);
    for (int from = 0; from < CELLS; from++) {
      for (int to = 0; to < CELLS; to++) {
        if (from != to) {
          moves.add(new ChessMove(from, to, 0));
        } else {
          moves.add(null);
        }
      }
    }

    promotions = new ArrayList<>(8 * 2 * SIZE);
    for (int promote = 0; promote < 8; promote++) {
      for (int color = 0; color < 2; color++) {
        for (int col = 0; col < SIZE; col++) {
          int from, to;
          if (color) {
            from = col * SIZE + SIZE - 2;
            to = col * SIZE + SIZE - 1;
          } else {
            from = col * SIZE + 1;
            to = col * SIZE;
          }
          promotions.add(new ChessMove(from, to, promote));
        }
      }
    }
  }

  private ChessMove(int from, int to, byte promote) {
    this.from = from;
    this.to = to;
  }

  static ChessMove of(int from, int to) {
    return moves.get(from * CELLS + to);
  }

  static ChessMove of(int from, int to, byte promote) {
  }
  
  public String toString() {
    return Chess.idxToCoords(from) + "-" + Chess.idxToCoords(to) +
        PIECE_LETTER[promote];
  }
}
