package gamer.gomoku;

import gamer.def.Game;
import gamer.def.Position;

import java.util.HashMap;
import java.util.Map;

public final class Gomoku implements Game<GomokuState, GomokuMove> {
  private static final int DEFAULT_SIZE = 19;
  private static final Map<Integer, Gomoku> INSTANCES = new HashMap<>();
  private final int size;
  private final Limits limits;

  private Gomoku(int size) {
    this.size = size;
    this.limits = new Limits(size);
  }

  public static Gomoku getInstance(int size) {
    if (INSTANCES.containsKey(size)) {
      return INSTANCES.get(size);
    }

    GomokuMove.createInstances(size);
    Gomoku game = new Gomoku(size);
    INSTANCES.put(size, game);
    return game;
  }

  public static Gomoku getInstance() {
    return getInstance(DEFAULT_SIZE);
  }

  @Override
  public GomokuState newGame() {
    return new GomokuState(size, limits);
  }

  @Override
  public GomokuState.RandomSelector getRandomMoveSelector() {
    return new GomokuState.RandomSelector();
  }

  int getSize() {
    return size;
  }
}
