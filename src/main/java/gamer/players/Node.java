package gamer.players;

import gamer.def.Move;
import gamer.def.Position;
import gamer.def.Solver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import static gamer.players.Sampler.PAYOFF_SCALE_FACTOR;

abstract class Node<P extends Position<P, M>, M extends Move> {
  @SuppressWarnings("rawtypes")
  static final DummyNode KNOW_EXACT = new DummyNode();
  @SuppressWarnings("rawtypes")
  static final DummyNode NO_CHILDREN = new DummyNode();

  protected final NodeContext<P, M> context;
  private final M move;
  private final Node<P, M> parent;
  protected List<Node<P, M>> children = null;
  private boolean knowExact = false;
  /** Payoff for player 0 */
  private double payoff;
  private int totalSamples = 0;
  private int pendingSamples = 0;
  private int player = -1;

  private Node() {
    move = null;
    parent = null;
    context = null;
  }

  Node(Node<P, M> parent, P state, M move, NodeContext<P, M> context) {
    this.context = context;
    this.parent = parent;
    this.move = move;

    assert state != null;

    if (state.isTerminal()) {
      this.player = -1;
      this.payoff = state.getPayoff(0);
      this.knowExact = true;
    } else {
      this.player = state.getPlayer();
    }

    if (!knowExact && context.solver != null) {
      Solver.Result<M> result = context.solver.solve(state);
      if (result != null) {
        this.payoff =
            result.payoff * Math.pow(PAYOFF_SCALE_FACTOR, result.moves);
        this.knowExact = true;
      }
    }
  }

  abstract protected Node<P, M> selectChild();

  abstract protected boolean maybeInitChildren(P state);

  protected int getPlayer() {
    return player;
  }

  protected Node<P, M> getRandomChild() {
    throw new RuntimeException("getRandomChild not implemented");
  }

  final Node<P, M> getParent() {
    return parent;
  }

  final M getMove() {
    return move;
  }

  final synchronized Collection<Node<P, M>> getChildren() {
    if (children == null) {
      throw new RuntimeException(
          "Requested children from a node without children.");
    }
    return children;
  }

  final double getPayoff() {
    return payoff;
  }

  final boolean knowExact() {
    return knowExact;
  }

  final synchronized int getSamples() {
    return totalSamples - pendingSamples;
  }

  // Samples, including pending.
  final int getTotalSamples() {
    return totalSamples;
  }

  final Node<P, M> selectChildOrAddPending(P state, int nsamples) {
    boolean learnedExact = false;
    boolean knowExactLocal = false;

    synchronized(this) {
      if (!knowExact) {
        pendingSamples += nsamples;
        learnedExact = children == null &&
                       maybeInitChildren(state) &&
                       context.propagateExact &&
                       checkChildrenForExact();
      }

      // totalSamples should be incremented _after_ maybeInitChildren() is
      // called since it may take into account the old totalSamples value.
      totalSamples += nsamples;
      knowExact = knowExact || learnedExact;

      if (!knowExact && children == null) {
        @SuppressWarnings("unchecked")
        Node<P, M> noChildrenResult = NO_CHILDREN;
        return noChildrenResult;
      }
      knowExactLocal = knowExact;
    }

    if (knowExact) {
      if (knowExactLocal && parent != null) {
        parent.addSamplesAndUpdate(nsamples, payoff, this, learnedExact);
      }
      @SuppressWarnings("unchecked")
      Node<P, M> knowExactResult = KNOW_EXACT;
      return knowExactResult;
    }

    return selectChild();
  }

  final void addSamples(int nsamples, double value) {
    addSamplesAndUpdate(nsamples, value, null, false);
  }

  final double getUcbPriority(double parentSamplesLog, boolean player) {
    if (totalSamples == 0) {
      return context.game.getMaxPayoff() + parentSamplesLog;  //
    }

    double payoff = player ? getPayoff() : -getPayoff();
    return payoff + context.payoffSpread *
        Math.sqrt(1.2 * parentSamplesLog / totalSamples);
  }

  @Override
  public final String toString() {
    return toString(null, 0, context.game.getMinPayoff());
  }

  synchronized final String toStringNested(P state, int nnodes) {
    int samplesLo = 0;
    int samplesHi = getTotalSamples();

    while (samplesHi - samplesLo > 1) {
      int samplesMid = (samplesHi + samplesLo) / 2;
      int nodesAboveThreshold = 0;

      Queue<Node<P, M>> queue = new ArrayDeque<>();
      queue.add(this);

      while (nodesAboveThreshold <= nnodes) {
        Node<P, M> node = queue.poll();
        if (node == null) {
          break;
        }
        if (node.getTotalSamples() < samplesMid) {
          continue;
        }
        nodesAboveThreshold++;
        if (node.children != null) {
          for (Node<P, M> child : node.children) {
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

    return toString(state, 0, samplesHi);
  }

  synchronized private String toString(P state, int indent, double minSamples) {
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
        " %s%.3f %d", knowExact() ? "=" : "", getPayoff(),
        totalSamples - pendingSamples));
    if (pendingSamples > 0) {
      builder.append(String.format(" + %d", pendingSamples));
    }
    if (children != null) {
      List<Node<P, M>> childrenAboveThreshold = new ArrayList<>();
      for (Node<P, M> child : children) {
        if (child.getTotalSamples() >= minSamples) {
          childrenAboveThreshold.add(child);
        }
      }

      Collections.sort(childrenAboveThreshold, (Node<P, M> n1, Node<P, M> n2)
          -> (n2.getTotalSamples() - n1.getTotalSamples()));

      for (Node<P, M> child : childrenAboveThreshold) {
        P nextState = state.clone();
        nextState.play(child.getMove());
        builder.append(child.toString(nextState, indent + 2, minSamples));
      }
    }

    return builder.toString();
  }

  private void addSamplesAndUpdate(
      int nsamples, double value, Node<P, M> child,
      boolean childLearnedExactValue) {
    boolean learnedExactValue = false;

    synchronized(this) {
      payoff = (payoff * getSamples() + value * nsamples) /
               (getSamples() + nsamples);

      pendingSamples -= nsamples;
      assert pendingSamples >= 0;

      if (context.propagateExact && childLearnedExactValue) {
        assert child != null;
        assert child.knowExact();
        learnedExactValue = checkChildrenForExact();
      }
    }

    if (parent != null)
      parent.addSamplesAndUpdate(
          nsamples, PAYOFF_SCALE_FACTOR * value, this, learnedExactValue);
  }

  private boolean checkChildrenForExact() {
    if (player < 0) {
      return false;
    }

    double lo = 1E10;
    double hi = -1E10;

    for (Node<P, M> child : children) {
      if (!child.knowExact)
        return false;
      double v = child.payoff;
      if (v < lo)
        lo = v;
      if (v > hi)
        hi = v;
    }

    payoff = PAYOFF_SCALE_FACTOR * (player == 0 ? hi : lo);
    knowExact = true;
    return true;
  }

  @SuppressWarnings("rawtypes")
  private final static class DummyNode extends Node {
    @Override
    protected DummyNode selectChild() {
      throw new RuntimeException("shouldn't be called");
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected boolean maybeInitChildren(Position state) {
      throw new RuntimeException("shouldn't be called");
    }
  }
}
