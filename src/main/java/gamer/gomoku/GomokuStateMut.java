package gamer.gomoku;

import gamer.def.IllegalMoveException;
import gamer.def.PositionMut;
import gamer.def.TerminalPositionException;
import gamer.util.GameStatusInt;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public final class GomokuStateMut
    implements PositionMut<GomokuStateMut, GomokuMove> {
  private BitSet marked;
  private BitSet markedx;
  private int status;
  private Gomoku game;

  GomokuStateMut(Gomoku game) {
    this.game = game;
    marked = new BitSet(game.getPoints());
    markedx = new BitSet(game.getPoints());
    status = GameStatusInt.init();
  }

  private GomokuStateMut(GomokuStateMut other) {
    game = other.game;
    marked = (BitSet) other.marked.clone();
    markedx = (BitSet) other.markedx.clone();
    status = other.status;
  }

  @Override
  public boolean isTerminal() {
    return GameStatusInt.isTerminal(status);
  }

  @Override
  public GomokuStateMut play(GomokuMove move) {
    GomokuStateMut next = new GomokuStateMut(this);
    next.apply(move);
    return next;
  }

  @Override
  public void apply(GomokuMove move) {
    if (isTerminal()) {
      throw new IllegalMoveException(this, move, "state is terminal");
    }

    if (marked.get(move.point)) {
      throw new IllegalMoveException(this, move, "point is not empty");
    }

    marked.set(move.point);
    if (getPlayerBool()) {
      markedx.set(move.point);
    }

    status = updateStatus(getPlayerBool(), move);
  }

  @Override
  public void reset() {
    marked.clear();
    markedx.clear();
    status = GameStatusInt.init();
  }

  @Override
  public List<GomokuMove> getMoves() {
    List<GomokuMove> moves = new ArrayList<>();
    for (int i = 0; i < game.getPoints(); i++) {
      if (!marked.get(i)) {
        moves.add(GomokuMove.of(i));
      }
    }

    return moves;
  }

  @Override
  public GomokuMove getRandomMove(Random random) {
    if (isTerminal())
      throw new TerminalPositionException();

    int i;
    do {
      i = random.nextInt(game.getPoints());
    } while (marked.get(i));

    return GomokuMove.of(i);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < game.getPoints(); i++) {
      if (marked.get(i)) {
        if (markedx.get(i)) {
          builder.append('X');
        } else {
          builder.append('O');
        }
      } else {
        builder.append('.');
      }

      if (i % game.getSize() == game.getSize() - 1) {
        builder.append('\n');
      } else {
        builder.append(' ');
      }
    }

    return builder.toString();
  }

  private boolean checkLine(int center, int left, int right, int delta) {
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

  private int updateStatus(boolean player, GomokuMove move) {
    int cell = move.point;
    boolean won = checkLine(cell, game.limLeft[cell], game.limRight[cell], 1)
        || checkLine(cell, game.limTop[cell], game.limBottom[cell], game.getSize())
        || checkLine(cell, game.limLT[cell], game.limRB[cell], game.getSize() + 1)
        || checkLine(cell, game.limRT[cell], game.limLB[cell], game.getSize() - 1);


    int status = GameStatusInt.init();
    if (!player)
      status = GameStatusInt.switchPlayer(status);
    if (won) {
      status = GameStatusInt.setPayoff(status, player ? 1 : -1);
    } else if (marked.nextClearBit(0) == game.getPoints()) {
      status = GameStatusInt.setPayoff(status, 0);
    }

    return status;
  }

  @Override
  public String moveToString(GomokuMove move) {
    return move.toString();
  }

  @Override
  public int getPlayer() {
    return getPlayerBool() ? 0 : 1;
  }

  @Override
  public boolean getPlayerBool() {
    return GameStatusInt.getPlayerBool(status);
  }

  @Override
  public int getPayoff(int player) {
    return GameStatusInt.getPayoff(status, player);
  }

  @Override
  public GomokuMove parseMove(String moveStr) {
    return GomokuMove.of(moveStr, game.getSize());
  }

}
