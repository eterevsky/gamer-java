package gamer.chess;

import static gamer.chess.Chess.CELLS;
import static gamer.chess.Chess.SIZE;
import static gamer.chess.Util.cr2i;
import static gamer.chess.Util.i2a;
import static gamer.chess.Util.i2col;
import static gamer.chess.Util.i2row;

import gamer.def.Move;

import java.util.ArrayList;
import java.util.List;

public final class ChessMove implements Move<Chess> {
  private static String[] PIECE_LETTER = {"", null, "R", "N", "B", "Q", "K"};
  private static int POSSIBLE_PROMOTIONS = 4;

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
          moves.add(new ChessMove(from, to, (byte)0));
        } else {
          moves.add(null);
        }
      }
    }

    promotions = new ArrayList<>(POSSIBLE_PROMOTIONS * 2 * 8);
    for (byte promote = 0; promote < POSSIBLE_PROMOTIONS; promote++) {
      for (int color = 0; color < 2; color++) {
        for (int col = 1; col <= 8; col++) {
          int from, to;
          if (color == 0) {
            from = cr2i(col, 7);
            to = cr2i(col, 8);
          } else {
            from = cr2i(col, 2);
            to = cr2i(col, 1);
          }
          promotions.add(new ChessMove(from, to, (byte)(promote + 2)));
        }
      }
    }
  }

  private ChessMove(int from, int to, byte promote) {
    this.from = from;
    this.to = to;
    this.promote = promote;
  }

  static ChessMove of(String from, String to, String promote) {
    byte promote;
    return of(a2i(from), a2i(to), a2piece(promote));
  }

  static ChessMove of(int from, int to) {
    return moves.get(from * CELLS + to);
  }

  static ChessMove of(String from, String to) {
    return of(a2i(from), a2i(to));
  }

  static ChessMove of(int from, int to, byte promote) {
    int color = i2row(to) == 8 ? 0 : 1;
    return promotions.get((promote - 2) * 2 * SIZE + color * SIZE +
                          i2col(to) - 1);
  }

  public String toString() {
    return i2a(from) + "-" + i2a(to) + PIECE_LETTER[promote];
  }
}
