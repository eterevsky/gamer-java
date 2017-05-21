package gamer.go;

import gamer.def.Game;
import gamer.def.MoveSelector;
import gamer.def.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Go implements Game<GoState, GoMove> {
  static final int SIZE = 19;
  static final int POINTS = SIZE * SIZE;
  private static final Go INSTANCE = new Go();
  private static final GoState.RandomSelector RANDOM_SELECTOR =
      new GoState.RandomSelector();
  static final int[][] NEIGHBORS = generateNeighbors();

  private Go() {}

  public static Go getInstance() {
    return INSTANCE;
  }

  static int[][] generateNeighbors() {
    int[][] neighbors = new int[POINTS][];
    for (int i = 0; i < POINTS; i++) {
      List<Integer> neighborsList = new ArrayList<>();
      if (i >= SIZE) neighborsList.add(i - SIZE);
      if (i < POINTS - SIZE) neighborsList.add(i + SIZE);
      if (i % SIZE != 0) neighborsList.add(i - 1);
      if (i % SIZE != SIZE - 1) neighborsList.add(i + 1);
      neighbors[i] = new int[neighborsList.size()];
      for (int j = 0; j < neighborsList.size(); j++) {
        neighbors[i][j] = neighborsList.get(j);
      }
    }

    return neighbors;
  }

  @Override
  public GoState newGame() {
    return new GoState();
  }

  @Override
  public MoveSelector<GoState, GoMove> getMoveSelector(String selector) {
    switch (selector) {
        case "random": return RANDOM_SELECTOR;
      default:
        throw new IllegalArgumentException();
    }
  }

  @Override
  public GoState.RandomSelector getRandomMoveSelector() {
    return RANDOM_SELECTOR;
  }
}
