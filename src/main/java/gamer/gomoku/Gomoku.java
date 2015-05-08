package gamer.gomoku;

import static java.lang.Math.min;

import gamer.def.Game;
import gamer.def.Position;

import java.util.HashMap;
import java.util.Map;

public final class Gomoku implements Game {
  public static Gomoku getInstance(int size) {
    if (INSTANCES.containsKey(size)) {
      return INSTANCES.get(size);
    }

    Gomoku game = new Gomoku(size);
    INSTANCES.put(size, game);
    return game;
  }

  public static Gomoku getInstance() {
    return getInstance(DEFAULT_SIZE);
  }

  @Override
	public <P extends PositionMut<P, GomokuMove>> P newGame() {
    return size == 19 ? new GomokuStateMut19() : new GomokuStateMut(this);
  }

	static final int DEFAULT_SIZE = 19;
  private static final Map<Integer, Gomoku> INSTANCES = new HashMap<>();

  private final int size;
  private final int points;
	
  final int[] limLeft, limRight;
  final int[] limTop, limBottom;
  final int[] limLT, limRB;
  final int[] limRT, limLB;

  private Gomoku(int size) {
    this.size = size;
    this.points = size * size;
    this.initialState = new GomokuState(this);

    limLeft = new int[points];
    limRight = new int[points];
    limTop = new int[points];
    limBottom = new int[points];
    limLT = new int[points];
    limRB = new int[points];
    limRT = new int[points];
    limLB = new int[points];

    createLimitTables();
  }

  private void createLimitTables() {
    for (int cell = 0; cell < points; cell++) {
      int x = cell % size;
      int y = cell / size;

      int left = min(x, 4);
      int right = min(size - x - 1, 4);
      int top = min(y, 4);
      int bottom = min(size - y - 1, 4);

      limLeft[cell] = cell - left;
      limRight[cell] = cell + right;
      limTop[cell] = cell - size * top;
      limBottom[cell] = cell + size * bottom;
      limLT[cell] = cell - (size + 1) * min(left, top);
      limRB[cell] = cell + (size + 1) * min(right, bottom);
      limRT[cell] = cell - (size - 1) * min(right, top);
      limLB[cell] = cell + (size - 1) * min(left, bottom);
    }
  }
	
  int getSize() {
    return size;
  }

  int getPoints() {
    return points;
  }
}
