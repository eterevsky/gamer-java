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

  @Override
  public GomokuState newGame() {
    return INITIAL_STATE;
  }

  @Override
  public GomokuStateMut newGameMut() {
    return new GomokuStateMut();
  }

  @Override
  public int getPlayersCount() {
    return 2;
  }

  @Override
  public boolean hasRandomPlayer() {
  return false;
  }
}
