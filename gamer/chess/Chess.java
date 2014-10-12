package gamer.chess;

import gamer.def.Game;

public final class Chess implements Game<Chess> {
  private static final Chess INSTANCE = new Chess();

  private Chess() {}

  public static Chess getInstance() {
    return INSTANCE;
  }

  public ChessState newGame() {
    return new ChessState();
  }
}
