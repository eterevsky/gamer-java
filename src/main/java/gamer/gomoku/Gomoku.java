package gamer.gomoku;

import gamer.def.Game;

public final class Gomoku implements Game<Gomoku> {
  static final int SIZE = 19;
  static final int POINTS = SIZE * SIZE;
  private static final Gomoku INSTANCE = new Gomoku();

  private Gomoku() {}

  public static Gomoku getInstance() {
    return INSTANCE;
  }

  public GomokuState newGame() {
    return new GomokuState();
  }
}
