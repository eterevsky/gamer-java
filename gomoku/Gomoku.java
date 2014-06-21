package gamer.gomoku;

import gamer.def.Game;

public final class Gomoku implements Game<Gomoku> {
  static final int SIZE = 19;

  public static GomokuState newGame() {
    return new GomokuState();
  }
}
