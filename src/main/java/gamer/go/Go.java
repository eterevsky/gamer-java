package gamer.go;

import gamer.def.Game;
import gamer.def.MoveSelector;
import gamer.def.Position;

import java.util.HashMap;
import java.util.Map;

public final class Go implements Game<GoState, GoMove> {
  static final int SIZE = 19;
  static final int POINTS = SIZE * SIZE;
  private static final Map<Integer, Gomoku> INSTANCE = new Go();
  private static final GoState.RandomSelector RANDOM_SELECTOR = new GoState.RandomSelector();
  static final int[][] NEIGHBORS = generateNeighbors();

  private Gomoku() {}

  public static Gomoku getInstance() {
    return INSTANCE;
  }

  static int[][] generateNeighbors() {
    int[][] neighbors = new int[POINTS][];
    for (int i = 0; i < POINTS; i++) {
      List<Integer> neighborsList = new ArrayList<>();
      if (i >= SIZE) neighborsList.add(i - SIZE);
      if (i < POINTS - SIZE) neighborsList.add(i + SIZE);
      if (i % SIZE != 0) neightbosList.add(i - 1);
      if (i % SIZE != SIZE - 1) neightbosList.add(i + 1);
      neighbors[i] = new int[neightbosList.size()];
      neightbosList.toArray(neighbors[i]);
    }

    return neighbors;
  }

  @Override
  public GoState newGame() {
    return new GoState();
  }

  @Override
  public MoveSelector<GomokuState, GomokuMove> getMoveSelector(String selector) {
    switch (selector) {
      case "random": return randomSelector;
      default:
        throw new IllegalArgumentException();
    }
  }

  @Override
  public GomokuState.RandomSelector getRandomMoveSelector() {
    return RANDOM_SELECTOR;
  }
}
