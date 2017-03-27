package gamer.gomoku;

import gamer.def.Game;
import gamer.def.MoveSelector;

import java.util.HashMap;
import java.util.Map;

public final class Gomoku implements Game<GomokuState, GomokuMove> {
  private static final int DEFAULT_SIZE = 19;
  private static final Map<Integer, Gomoku> INSTANCES = new HashMap<>();
  private final int size;
  private final Limits limits;
  private final GomokuState.RandomSelector randomSelector;
  private final GomokuState.RandomNeighborSelector randomNeighborSelector;

  private Gomoku(int size) {
    this.size = size;
    this.limits = new Limits(size);

    randomSelector = new GomokuState.RandomSelector(size);
    randomNeighborSelector = new GomokuState.RandomNeighborSelector(size);
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
  public MoveSelector<GomokuState, GomokuMove> getMoveSelector(String selector) {
    switch (selector) {
      case "random": return randomSelector;
      case "neighbor": return randomNeighborSelector;
      default:
        throw new IllegalArgumentException();
    }
  }

  @Override
  public GomokuState.RandomSelector getRandomMoveSelector() {
    return randomSelector;
  }

  public GomokuState.RandomNeighborSelector getRandomNeighborSelector() {
    return randomNeighborSelector;
  }

  int getSize() {
    return size;
  }
}
