package gamer.mcts;

import gamer.def.Game;
import gamer.def.Move;
import gamer.def.State;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Node<S extends State<S, M>, M extends Move> {
  static class Context<S extends State<S, M>, M extends Move> {
    private final int minPayoff;
    private final int maxPayoff;
    private final int payoffSpread;

    Context(Game<S, M> game) {
      minPayoff = game.getMinPayoff();
      maxPayoff = game.getMaxPayoff();
      payoffSpread = maxPayoff - minPayoff;
    }
  }

  private final Context<S, M> context;
  private final Node<S, M> parent;
  private AtomicReference<List<Node<S, M>>> children = new AtomicReference<>();
  private final M move;
  private final int player;

  private boolean exact = false;
  private int exactPayoff = 0;
  private volatile int totalSamples = 0;
  private AtomicInteger pendingSamples = new AtomicInteger(0);
  // Sum of the payoffs in case exact = false, or a single payoff if true.
  // Payoff is calculated for player 0.
  private volatile long totalPayoff = 0;
  private volatile long totalPayoffSquares = 0;

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

  public int getPlayer() {
    return player;
  }

  final Node<S, M> getParent() {
    return parent;
  }

  final boolean hasChildren() {
    return getChildren() != null;
  }

  final List<Node<S, M>> getChildren() {
    return children.get();
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
      newChildren .add(new Node<S, M>(this.context, this, stateClone, move));
    }
    children.compareAndSet(null, newChildren);
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
    return totalSamples;
  }

  final int getCompleteSamples() {
    return totalSamples - pendingSamples.get();
  }

  final long getPayoffSum() {
    return exact ? exactPayoff * (totalSamples - pendingSamples.get())
                 : totalPayoff;
  }

  final long getPayoffSquaresSum() {
    return exact ? exactPayoff * exactPayoff *
                   (totalSamples - pendingSamples.get()) : totalPayoffSquares;
  }

  final void addExactSamples(int count) {
    totalSamples += count;
  }

  final void addPendingSamples(int count) {
    pendingSamples.getAndAdd(count);
    totalSamples += count;
  }

  final void addSamples(int count, int payoffSum, long payoffSquaresSum) {
    assert count <= pendingSamples.get();
    pendingSamples.getAndAdd(-count);
    totalPayoff += payoffSum;
    totalPayoffSquares += payoffSquaresSum;
  }

  M getMove() {
    return move;
  }

  double getPayoff() {
    return exact ? exactPayoff
                 : (double) totalPayoff / (totalSamples - pendingSamples.get());
  }

  double getBiasedPayoff() {
    return exact ? exactPayoff : (double) (totalPayoff + context.minPayoff *
                                                         (pendingSamples.get() +
                                                          1)) /
                                 (totalSamples + 1);
  }

  double getBiasedVariance(double mean) {
    if (exact) return 0;
    double meanSquare = (double) (totalPayoffSquares +
                                  context.minPayoff * context.minPayoff *
                                  (pendingSamples.get() + 1)) /
                        (totalSamples + 1);
    return meanSquare - mean * mean;
  }

  double getBiasedScore(double logParentSamples, boolean reverse) {
    double mean = getBiasedPayoff();
    double variance = getBiasedVariance(mean);
    double coefficient = logParentSamples / (totalSamples + 1);
    // This works only for 2-player 0-sum games.
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
    int samplesHi = totalSamples;

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
        if (node.totalSamples < samplesMid) {
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
                                 totalSamples - pendingSamples.get()));
    if (pendingSamples.get() > 0) {
      builder.append(String.format(" + %d", pendingSamples.get()));
    }
    if (hasChildren()) {
      List<Node<S, M>> childrenAboveThreshold = new ArrayList<>();
      for (Node<S, M> child : getChildren()) {
        if (child.totalSamples >= minSamples) {
          childrenAboveThreshold.add(child);
        }
      }

      Collections.sort(childrenAboveThreshold,
                       (Node<S, M> n1, Node<S, M> n2) -> (n2.totalSamples -
                                                          n1.totalSamples));

      for (Node<S, M> child : childrenAboveThreshold) {
        S nextState = state.clone();
        nextState.play(child.move);
        builder.append(child.toStringNested(nextState, indent + 2, minSamples));
      }
    }

    return builder.toString();
  }
}
