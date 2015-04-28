package gamer.gomoku;

import static java.lang.Math.min;

import gamer.def.Game;
import gamer.def.PositionMut;
import gamer.util.GameStatusInt;

import java.util.BitSet;
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
  public GomokuState newGame() {
    return initialState;
  }

  @Override
  public PositionMut<?, GomokuMove> newGameMut() {
    return size == 19 ? new GomokuStateMut19() : new GomokuStateMut(this);
//    return new GomokuStateMut(this);
  }

  @Override
  public int getPlayersCount() {
    return 2;
  }

  @Override
  public boolean hasRandomPlayer() {
  return false;
  }

	static final int DEFAULT_SIZE = 19;
  private static final Map<Integer, Gomoku> INSTANCES = new HashMap<>();

  private final int size;
  private final int points;
  private final GomokuState initialState;
	
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
	
  private boolean checkLine(
			BitSet marked, BitSet markedx, int center, int left, int right,
	    int delta) {
    boolean player = markedx.get(center);
    int l = 1;
    for (int cell = center - delta; cell >= left; cell -= delta) {
      if (!marked.get(cell) || markedx.get(cell) != player)
        break;
      l++;
    }

    for (int cell = center + delta; cell <= right; cell += delta) {
      if (!marked.get(cell) || markedx.get(cell) != player)
        break;
      l++;
    }

    return l >= 5;
  }

  int getStatus(
			BitSet marked, BitSet markedx, boolean player, GomokuMove move) {
    int cell = move.point;
    boolean won =
			     checkLine(marked, markedx, cell, limLeft[cell], limRight[cell], 1)
        || checkLine(marked, markedx, cell, limTop[cell], limBottom[cell], size)
        || checkLine(marked, markedx, cell, limLT[cell], limRB[cell], size + 1)
        || checkLine(marked, markedx, cell, limRT[cell], limLB[cell], size - 1);

    int status = GameStatusInt.init();
    if (!player)
      status = GameStatusInt.switchPlayer(status);

    if (won) {
      status = GameStatusInt.setPayoff(status, player ? -1 : 1);
    } else if (marked.nextClearBit(0) == points) {
      status = GameStatusInt.setPayoff(status, 0);
    }
    return status;
  }

  int getSize() {
    return size;
  }

  int getPoints() {
    return points;
  }
}
