package gamer.gomoku;

import gamer.def.Game;
import gamer.def.MoveSelector;
import gamer.def.Position;

import java.util.HashMap;
import java.util.Map;

public final class Gomoku implements Game<GomokuState, GomokuMove> {
  private static final int DEFAULT_SIZE = 19;
  private static final Map<Integer, Gomoku> INSTANCES = new HashMap<>();
  private final int size;
  private final Limits limits;
  private final GomokuState.RandomSelector random_selector;
  private final GomokuState.RandomNeighborSelector random_neighbor_selector;

  private Gomoku(int size) {
    this.size = size;
    this.limits = new Limits(size);

    random_selector = new GomokuState.RandomSelector(size);
    random_neighbor_selector = new GomokuState.RandomNeighborSelector(size);
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
      case "random": return random_selector;
      case "neighbor": return random_neighbor_selector;
      default:
        throw new IllegalArgumentException();
    }
  }

  @Override
  public GomokuState.RandomSelector getRandomMoveSelector() {
    return random_selector;
  }

  public GomokuState.RandomNeighborSelector getRandomNeighborSelector() {
    return random_neighbor_selector;
  }

  int getSize() {
    return size;
  }
}
