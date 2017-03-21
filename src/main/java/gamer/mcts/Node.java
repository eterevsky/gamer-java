package gamer.mcts;

import gamer.def.Move;
import gamer.def.State;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public class Node<S extends State<S, M>, M extends Move> {
  private final Node<S, M> parent;
  private List<Node<S, M>> children = null;
  private final M move;
  private int player = -2;

  private boolean exact = false;
  private int totalSamples = 0;
  private int pendingSamples = 0;
  // Sum of the payoffs in case exact = false, or a single payoff if true.
  // Payoff is calculated for player 0.
  private long totalPayoff = 0;

  Node(Node<S, M> parent, S state, M move) {
    this.parent = parent;
    this.move = move;

    assert state != null;

    if (state.isTerminal()) {
      this.totalPayoff = state.getPayoff(0);
      this.exact = true;
    } else {
      this.player = state.getPlayer();
    }
  }

  final boolean hasChildren() {
    return children != null && children.size() > 0;
  }

  final List<Node<S, M>> getChildren() {
    return children;
  }

  final boolean hasExactPayoff() {
    return exact;
  }

  final int getTotalSamples() {
    return totalSamples;
  }

  M getMove() {
    return move;
  }

  double getPayoff() {
    return exact ? totalPayoff
                 : (double)totalPayoff / (totalSamples - pendingSamples);
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

    return toStringNested(state, 0, samplesHi);
  }

  synchronized private String toStringNested(S state, int indent, double
      minSamples) {
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
    builder.append(String.format(
        " %s%.3f %d", exact ? "=" : "", getPayoff(),
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

      Collections.sort(
          childrenAboveThreshold,
          (Node<S, M> n1, Node<S, M> n2) ->
              (n2.totalSamples - n1.totalSamples));

      for (Node<S, M> child : childrenAboveThreshold) {
        S nextState = state.clone();
        nextState.play(child.move);
        builder.append(child.toStringNested(nextState, indent + 2, minSamples));
      }
    }

    return builder.toString();
  }
}
