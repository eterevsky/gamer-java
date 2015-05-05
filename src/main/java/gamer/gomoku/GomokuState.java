package gamer.gomoku;

import gamer.def.IllegalMoveException;
import gamer.def.Position;
import gamer.def.PositionMut;
import gamer.def.TerminalPositionException;
import gamer.util.GameStatusInt;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public final class GomokuState implements Position<GomokuState, GomokuMove> {
  private final BitSet marked;
  private final BitSet markedx;
  private final int status;
  private final Gomoku game;

  GomokuState(Gomoku game) {
    this.game = game;
    marked = new BitSet(game.getPoints());
    markedx = new BitSet(game.getPoints());
    status = GameStatusInt.init();
  }

  private GomokuState(GomokuState other, GomokuMove move) {
    game = other.game;
    marked = (BitSet) other.marked.clone();
    marked.set(move.point);
    if (other.getPlayerBool()) {
      markedx = (BitSet) other.markedx.clone();
      markedx.set(move.point);
    } else {
      markedx = other.markedx;
    }
    status = this.game.getStatus(marked, markedx, !other.getPlayerBool(), move);
  }
	
	@Override
	public GomokuStateMut toMutable() {
		// if (getSize() == 19) {
		// 	return new GomokuStateMut19(marked, markedx, status);
		// } else {
			return new GomokuStateMut(game, marked, markedx, status);
		// }
	}

  @Override
  public GomokuState play(GomokuMove move) {
    if (isTerminal()) {
      throw new TerminalPositionException();
    }

    if (marked.get(move.point)) {
      throw new IllegalMoveException(this, move, "point is not empty");
    }

    return new GomokuState(this, move);
  }
	
	@Override
	public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("GomokuState\n");
    for (int i = 0; i < getPoints(); i++) {
      if (marked.get(i)) {
				builder.append(markedx.get(i) ? 'X' : 'O');
      } else {
        builder.append('.');
      }

      if (i % getSize() == getSize() - 1) {
        builder.append('\n');
      } else {
        builder.append(' ');
      }
		}
		return builder.toString();
	}

  @Override
  public boolean isTerminal() {
    return GameStatusInt.isTerminal(status);
  }

  @Override
  public List<GomokuMove> getMoves() {
    List<GomokuMove> moves = new ArrayList<>();
    for (int i = 0; i < getPoints(); i++) {
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
      i = random.nextInt(getPoints());
    } while (marked.get(i));

    return GomokuMove.of(i);
  }


  @Override
  public String moveToString(GomokuMove move) {
    return move.toString(getSize());
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
    return GomokuMove.of(moveStr, getSize());
  }
	
	private int getPoints() {
		return game.getPoints();
	}
	
	private int getSize() {
		return game.getSize();
	}
}
