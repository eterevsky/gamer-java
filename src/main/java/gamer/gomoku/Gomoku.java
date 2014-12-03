package gamer.gomoku;

import gamer.def.Game;

public final class Gomoku implements Game {
  static final int SIZE = 19;
  static final int POINTS = SIZE * SIZE;
  private static final Gomoku INSTANCE = new Gomoku();
  private static final GomokuState INITIAL_STATE = new GomokuState();

  private Gomoku() {}

  public static Gomoku getInstance() {
    return INSTANCE;
  }

  public GomokuState newGame() {
    return INITIAL_STATE;
  }

  public GomokuStateMut newGameMut() {
    return new GomokuStateMut();
  }
}
