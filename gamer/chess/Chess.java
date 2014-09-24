package gamer.chess;

import gamer.def.Game;

public final class Chess implements Game<Chess> {
  static final int SIZE = 8;
  static final int CELLS = SIZE * SIZE;
  private static final Chess INSTANCE = new Chess();

  private Chess() {}

  public static Chess getInstance() {
    return INSTANCE;
  }

  public ChessState newGame() {
    return new ChessState();
  }

  static int coordsToIdx(String a) {
    assert a.length() == 2;
    return (a.charAt(0) - 'a') * SIZE + a.charAt(1) - '1';
  }

  static int idxToCoords(int idx) {
    return "" + ('a' + idx / SIZE) + ('0' + idx % SIZE);
  }
}
