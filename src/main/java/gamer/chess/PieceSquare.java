package gamer.chess;

import java.util.ArrayList;
import java.util.List;

final class PieceSquare implements Comparable<PieceSquare> {
  static private final List<PieceSquare> INSTANCES = createInstances();

  byte piece;
  int square;

  private PieceSquare(byte piece, int square) {
    this.piece = piece;
    this.square = square;
  }

  private static List<PieceSquare> createInstances() {
    List<PieceSquare> instances = new ArrayList<>((Pieces.MAX + 1) * 64);

    for (byte p = 0; p <= Pieces.MAX; p++) {
      for (int c = 0; c < 64; c++) {
        instances.add(new PieceSquare(p, c));
      }
    }

    return instances;
  }

  static PieceSquare of(byte piece, int square) {
    return INSTANCES.get(piece * 64 + square);
  }

  @Override
  public int compareTo(PieceSquare o) {
    int d = this.piece - o.piece;
    return d == 0 ? this.square - o.square : d;
  }
}
