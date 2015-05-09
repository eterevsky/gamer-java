package gamer.chess;

import gamer.def.Move;

import java.util.ArrayList;
import java.util.List;

import static gamer.chess.Board.*;
import static gamer.chess.Pieces.*;

public final class ChessMove implements Move {
  private static final int POSSIBLE_PROMOTIONS = 4;
  private static final List<ChessMove> moves = generateSimpleMoves();
  private static final List<ChessMove> promotions = generatePromotions();

  public final int from;
  public final int to;
  public final byte promote;

  private ChessMove(int from, int to, byte promote) {
    this.from = from;
    this.to = to;
    this.promote = promote;
  }

  static ChessMove of(String from, String to, String promote) {
    return of(a2i(from), a2i(to), a2piece(promote));
  }

  static ChessMove of(int from, int to) {
    return moves.get(from * 64 + to);
  }

  static ChessMove of(String from, String to) {
    return of(a2i(from), a2i(to));
  }

  static ChessMove of(int from, int to, byte promote) {
    int color = i2row(to) == 8 ? 0 : 1;
    return promotions.get((promote - 2) * 2 * 8 * 8 + color * 8 * 8 +
                          (i2col(from) - 1) * 8 + i2col(to) - 1);
  }

  private static List<ChessMove> generateSimpleMoves() {
    List<ChessMove> moves = new ArrayList<>(64 * 64);
    for (int from = 0; from < 64; from++) {
      for (int to = 0; to < 64; to++) {
        if (from != to) {
          moves.add(new ChessMove(from, to, Pieces.EMPTY));
        } else {
          moves.add(null);
        }
      }
    }

    return moves;
  }

  private static List<ChessMove> generatePromotions() {
    List<ChessMove> promotions = new ArrayList<>(POSSIBLE_PROMOTIONS * 2 * 8 * 8);
    for (byte promote = 0; promote < POSSIBLE_PROMOTIONS; promote++) {
      for (int color = 0; color < 2; color++) {
        for (int colFrom = 1; colFrom <= 8; colFrom++)
          for (int colTo = 1; colTo <= 8; colTo++) {
            int from, to;
            if (color == 0) {
              from = cr2i(colFrom, 7);
              to = cr2i(colTo, 8);
            } else {
              from = cr2i(colFrom, 2);
              to = cr2i(colTo, 1);
            }
            promotions.add(new ChessMove(from, to, (byte)(promote + 2)));
          }
      }
    }
    return promotions;
  }

  @Override
  public String toString() {
    return i2a(from) + "-" + i2a(to) +
           ((promote != EMPTY) ? piece2a(promote) : "");
  }
}
