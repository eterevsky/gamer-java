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
}
