package gamer.mcts;

import gamer.def.Game;
import gamer.def.Move;
import gamer.def.State;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

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
  private List<Node<S, M>> children = null;
  private final M move;
  private final int player;

  private boolean exact = false;
  private int exactPayoff = 0;
  private int totalSamples = 0;
  private int pendingSamples = 0;
  // Sum of the payoffs in case exact = false, or a single payoff if true.
  // Payoff is calculated for player 0.
  private long totalPayoff = 0;
  private long totalPayoffSquares = 0;

  Node(Context context, Node<S, M> parent, S state, M move) {
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
    return children != null && children.size() > 0;
  }

  final List<Node<S, M>> getChildren() {
    return children;
  }

  final Node<S, M> getChild(M move) {
    for (Node<S, M> child : children) {
      if (child.getMove().equals(move)) {
        return child;
      }
    }
    throw new RuntimeException("Requested a child node with unknown move.");
  }

  final synchronized void initChildren(S state) {
    if (hasChildren()) return;
    List<M> moves = state.getMoves();
    children = new ArrayList<>(moves.size());
    for (M move : moves) {
      S stateClone = state.clone();
      stateClone.play(move);
      children.add(new Node<S, M>(this.context, this, stateClone, move));
    }
  }

  final boolean hasExactPayoff() {
    return exact;
  }

  final int getExactPayoff() {
    return exactPayoff;
  }

  public int getPendingSamples() {
    return pendingSamples;
  }

  final int getTotalSamples() {
    return totalSamples;
  }

  final int getCompleteSamples() {
    return totalSamples - pendingSamples;
  }

  final long getPayoffSum() {
    return exact ? exactPayoff * (totalSamples - pendingSamples) : totalPayoff;
  }

  final long getPayoffSquaresSum() {
    return exact ? exactPayoff * exactPayoff * (totalSamples - pendingSamples)
                 : totalPayoffSquares;
  }

  final void addExactSamples(int count) {
    totalSamples += count;
  }

  final void addPendingSamples(int count) {
    pendingSamples += count;
    totalSamples += count;
  }

  final void addSamples(int count, int payoffSum, long payoffSquaresSum) {
    assert count <= pendingSamples;
    pendingSamples -= count;
    totalPayoff += payoffSum;
    totalPayoffSquares += payoffSquaresSum;
  }

  M getMove() {
    return move;
  }

  double getPayoff() {
    return exact ? exactPayoff
                 : (double) totalPayoff / (totalSamples - pendingSamples);
  }

  double getBiasedPayoff() {
    return exact ? exactPayoff :
           (double) (totalPayoff + context.minPayoff * (pendingSamples + 1)) /
           (totalSamples + 1);
  }

  double getBiasedVariance(double mean) {
    if (exact) return 0;
    double meanSquare = (double) (totalPayoffSquares +
                                  context.minPayoff * context.minPayoff *
                                  (pendingSamples + 1)) / (totalSamples + 1);
    return meanSquare - mean * mean;
  }

  synchronized double getBiasedScore(double logParentSamples, boolean reverse) {
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

  synchronized final String toStringNested(S state, int nnodes) {
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
        if (node.children != null) {
          for (Node<S, M> child : node.children) {
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

  synchronized private String toStringNested(
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
                                 totalSamples - pendingSamples));
    if (pendingSamples > 0) {
      builder.append(String.format(" + %d", pendingSamples));
    }
    if (children != null) {
      List<Node<S, M>> childrenAboveThreshold = new ArrayList<>();
      for (Node<S, M> child : children) {
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
