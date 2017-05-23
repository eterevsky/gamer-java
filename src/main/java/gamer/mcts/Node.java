package gamer.mcts;

import gamer.def.Game;
import gamer.def.Move;
import gamer.def.State;
import gamer.g2048.G2048;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Node<S extends State<S, M>, M extends Move> {
  static class Context<S extends State<S, M>, M extends Move> {
    private final int minPayoff;
    private final int minPayoffSquare;
    private final int maxPayoff;
    private final int payoffSpread;

    Context(Game<S, M> game) {
      minPayoff = game.getMinPayoff();
      minPayoffSquare = minPayoff * minPayoff;
      maxPayoff = game.getMaxPayoff();
      payoffSpread = maxPayoff - minPayoff;
    }
  }

  private final Context<S, M> context;
  private final Node<S, M> parent;
  private volatile List<Node<S, M>> children = null;
  private final M move;
  private final int player;

  private boolean exact = false;
  private int exactPayoff = 0;

  private final AtomicInteger totalSamples = new AtomicInteger();
  private final AtomicInteger pendingSamples = new AtomicInteger();

  // Sum of the payoffs in case exact = false, or a single payoff if true.
  // Payoff is calculated for player 0.
  private final AtomicLong totalPayoffBits =
      new AtomicLong(Double.doubleToLongBits(0));
  private final AtomicLong totalPayoffSquaresBits =
      new AtomicLong(Double.doubleToLongBits(0));

  Node(Context<S, M> context, Node<S, M> parent, S state, M move) {
    this.context = context;
    this.parent = parent;
    this.move = move;

    assert state != null;

    if (state.isTerminal()) {
      exactPayoff = state.getPayoff(0);
      exact = true;
      player = -2;
    } else {
      player = state.getPlayer();
    }
  }

  private double getTotalPayoff() {
    return Double.longBitsToDouble(totalPayoffBits.get());
  }

  private double getTotalPayoffSquares() {
    return Double.longBitsToDouble(totalPayoffSquaresBits.get());
  }

  private void addToPayoff(double payoff, double payoffSquares) {
    while (true) {
      long currentPayoffBits = totalPayoffBits.get();
      long newTotalPayoffBits = Double.doubleToLongBits(
          Double.longBitsToDouble(currentPayoffBits) + payoff);
      if (totalPayoffBits.compareAndSet(
              currentPayoffBits, newTotalPayoffBits)) break;
    }

    while (true) {
      long currentPayoffSquaresBits = totalPayoffSquaresBits.get();
      long newTotalPayoffSquaresBits = Double.doubleToLongBits(
          Double.longBitsToDouble(currentPayoffSquaresBits) + payoffSquares);
      if (totalPayoffSquaresBits.compareAndSet(
              currentPayoffSquaresBits, newTotalPayoffSquaresBits)) break;
    }
  }

  public int getPlayer() {
    return player;
  }

  final Node<S, M> getParent() {
    return parent;
  }

  final boolean hasChildren() {
    return children != null;
  }

  final List<Node<S, M>> getChildren() {
    return children;
  }

  final Node<S, M> getChild(M move) {
    for (Node<S, M> child : getChildren()) {
      if (child.getMove().equals(move)) {
        return child;
      }
    }
    throw new RuntimeException("Requested a child node with unknown move.");
  }

  final void initChildren(S state) {
    if (hasChildren()) return;
    List<M> moves = state.getMoves();
    List<Node<S, M>> newChildren = new ArrayList<>(moves.size());
    for (M move : moves) {
      S stateClone = state.clone();
      stateClone.play(move);
      assert move != null;
      newChildren.add(new Node<>(this.context, this, stateClone, move));
    }
    children = newChildren;
  }

  final boolean hasExactPayoff() {
    return exact;
  }

  final int getExactPayoff() {
    return exactPayoff;
  }

  public int getPendingSamples() {
    return pendingSamples.get();
  }

  final int getTotalSamples() {
    return totalSamples.get();
  }

  final int getCompleteSamples() {
    return totalSamples.get() - pendingSamples.get();
  }

  final double getPayoffSum() {
    return exact ? exactPayoff * (totalSamples.get() - pendingSamples.get())
                 : getTotalPayoff();
  }

  final double getPayoffSquaresSum() {
    return exact ? exactPayoff * exactPayoff *
                   (totalSamples.get() - pendingSamples.get()) : getTotalPayoffSquares();
  }

  final void addExactSamples(int count) {
    totalSamples.addAndGet(count);
  }

  final void addPendingSamples(int count) {
    totalSamples.addAndGet(count);
    pendingSamples.addAndGet(count);
  }

  final void addSamples(int count, double payoffSum, double payoffSquaresSum) {
    assert count <= pendingSamples.get();
    addToPayoff(payoffSum, payoffSquaresSum);
    pendingSamples.addAndGet(-count);
  }

  M getMove() {
    return move;
  }

  double getPayoff() {
    return exact ? exactPayoff
                 : getTotalPayoff() / (totalSamples.get() - pendingSamples.get());
  }

  double getBiasedScore(double logParentSamples, boolean reverse) {
    assert logParentSamples >= 0;

    double totalPayoff = getTotalPayoff();
    double totalPayoffSquares = getTotalPayoffSquares();
    long totalSamples = this.totalSamples.get() + 1;
    long pendingSamples = this.pendingSamples.get() + 1;

    double coefficient = logParentSamples / totalSamples;

    if (exact) {
      return (reverse ? -exactPayoff : exactPayoff) + 3 * context.payoffSpread * coefficient;
    }

    double mean =
        (totalPayoff + context.minPayoff * pendingSamples) / totalSamples;
    double variance =
        (totalPayoffSquares + context.minPayoffSquare * pendingSamples) /
            totalSamples -
        mean * mean;

    if (variance < 0) {
      variance = context.payoffSpread * context.payoffSpread / 4.0;
    }

    // reverse works only for 2-player 0-sum games.
    return (reverse ? -mean : mean) + Math.sqrt(2 * variance * coefficient) +
           3 * context.payoffSpread * coefficient;
  }

  @Override
  public final String toString() {
    return toStringNested(null, 16);
  }

  public final String toString(S state) {
    return toStringNested(state, 16);
  }

  final String toStringNested(S state, int nnodes) {
    int samplesLo = 0;
    int samplesHi = totalSamples.get();

    while (samplesHi - samplesLo > 1) {
      int samplesMid = (samplesHi + samplesLo) / 2;
      int nodesAboveThreshold = 0;

      Queue<Node<S, M>> queue = new ArrayDeque<>();
      queue.add(this);

      while (nodesAboveThreshold <= nnodes) {
        Node<S, M> node = queue.poll();
        if (node == null) {
          break;
        }
        if (node.totalSamples.get() < samplesMid) {
          continue;
        }
        nodesAboveThreshold++;
        if (node.hasChildren()) {
          for (Node<S, M> child : node.getChildren()) {
            queue.add(child);
          }
        }
      }

      if (nodesAboveThreshold >= nnodes) {
        samplesLo = samplesMid;
      }
      if (nodesAboveThreshold <= nnodes) {
        samplesHi = samplesMid;
      }
    }

    return toStringNested(state, 0, samplesLo);
  }

  private String toStringNested(
      S state, int indent, double minSamples) {
    StringBuilder builder = new StringBuilder();
    builder.append('\n');
    for (int i = 0; i < indent; i++) {
      builder.append(' ');
    }
    if (move != null && state != null) {
      builder.append(state.moveToString(move));
    } else {
      builder.append("root");
    }
    builder.append(String.format(" %s%.3f %d", exact ? "=" : "", getPayoff(),
                                 totalSamples.get() - pendingSamples.get()));
    if (pendingSamples.get() > 0) {
      builder.append(String.format(" + %d", pendingSamples));
    }
    if (hasChildren()) {
      List<Node<S, M>> childrenAboveThreshold = new ArrayList<>();
      for (Node<S, M> child : getChildren()) {
        if (child.totalSamples.get() >= minSamples) {
          childrenAboveThreshold.add(child);
        }
      }

      Collections.sort(childrenAboveThreshold,
                       (Node<S, M> n1, Node<S, M> n2) -> (n2.totalSamples.get() -
                                                          n1.totalSamples.get()));

      for (Node<S, M> child : childrenAboveThreshold) {
        S nextState = state;

        if (move != null) {
          nextState = state.clone();
          nextState.play(move);
        }

        builder.append(child.toStringNested(nextState, indent + 2, minSamples));
      }
    }

    return builder.toString();
  }
}
