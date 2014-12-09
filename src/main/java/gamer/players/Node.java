package gamer.players;

import gamer.def.Move;
import gamer.def.Position;
import gamer.def.Solver;

import java.util.Collection;
import java.util.List;

abstract class Node<P extends Position<P, M>, M extends Move> {
  private final P position;
  private final M move;
  private final Node<P, M> parent;
  protected final NodeContext<P, M> context;
  protected List<Node<P, M>> children = null;
  private boolean knowExact = false;
  private double payoff;  // Payoff for player 0
  private int totalSamples = 0;
  private int pendingSamples = 0;

  public static final class SelectChildResult<
      P extends Position<P, M>, M extends Move> {
    public Node<P, M> child;
    public final boolean knowExact;
    public final boolean noChildren;

    private SelectChildResult(boolean knowExact, boolean noChildren) {
      this.child = null;
      this.knowExact = knowExact;
      this.noChildren = noChildren;
    }

    private SelectChildResult(Node<P, M> child) {
      this.child = child;
      this.knowExact = false;
      this.noChildren = false;
    }
  }

  @SuppressWarnings("rawtypes")
  public static final SelectChildResult KNOW_EXACT =
      new SelectChildResult(true, false);

  @SuppressWarnings("rawtypes")
  public static final SelectChildResult NO_CHILDREN =
      new SelectChildResult(false, true);

  Node(Node<P, M> parent,
       P position,
       M move,
       NodeContext<P, M> context) {
    this.context = context;
    this.parent = parent;
    this.position = position;
    this.move = move;

    if (position != null) {
      if (position.isTerminal()) {
        this.payoff = position.getPayoff(0);
        this.knowExact = true;
      } else if (context.solver != null) {
        Solver.Result<M> result = context.solver.solve(position);
        if (result != null) {
          this.payoff = result.payoff;
          this.knowExact = true;
        }
      }
    }
  }

  abstract protected Node<P, M> selectChild();
  abstract protected boolean maybeInitChildren();

  final Node<P, M> getParent() {
    return parent;
  }

  final P getPosition() {
    return position;
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

  final synchronized double getPayoff() {
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

  final SelectChildResult<P, M> selectChildOrAddPending(int nsamples) {
    boolean learnedExact = false;

    synchronized(this) {
      totalSamples += nsamples;

      if (!knowExact) {
        pendingSamples += nsamples;
        learnedExact = children == null &&
                       maybeInitChildren() &&
                       context.propagateExact &&
                       checkChildrenForExact();

        if (!learnedExact) {
          return children == null ? new SelectChildResult<P, M>(false, true)
                                  : new SelectChildResult<P, M>(selectChild());
        }
      }
    }

    if (parent != null) {
      parent.addSamplesAndUpdate(nsamples, payoff, this, learnedExact);
    }

    return new SelectChildResult<P, M>(true, false);
  }

  final void addSamples(int nsamples, double value) {
    addSamplesAndUpdate(nsamples, value, null, false);
  }

  final double getUcbPriority(double parentSamplesLog, boolean player) {
    if (totalSamples == 0) {
      return 2 * (1 + Math.sqrt(parentSamplesLog));
    }

    double payoff = player ? getPayoff() : -getPayoff();
    return payoff + Math.sqrt(parentSamplesLog / totalSamples);
  }

  final Node<P, M> getChildByPositionForTest(P position) {
    if (children == null) {
      throw new RuntimeException(
          "Requested children from a node without children.");
    }
    for (Node<P, M> child : children) {
      if (child.position.equals(position))
        return child;
    }
    return null;
  }

  @Override
  public final String toString() {
    return toString(0);
  }

  private final String toString(int indent) {
    StringBuilder builder = new StringBuilder();
    builder.append('\n');
    for (int i = 0; i < indent; i++) {
      builder.append(' ');
    }
    if (move != null) {
      builder.append(move.toString());
    } else {
      builder.append(position.toString());
    }
    builder.append(String.format(
        " %.1f/%d/%d", getPayoff(), totalSamples, pendingSamples));
    if (knowExact()) {
      builder.append(" exact");
    }
    if (children != null) {
      for (Node<P, M> child : children) {
        builder.append(child.toString(indent + 2));
      }
    }

    return builder.toString();
  }

  private final void addSamplesAndUpdate(
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
        learnedExactValue = maybeSetExactValue(child);
      }
    }

    if (parent != null)
      parent.addSamplesAndUpdate(nsamples, value, this, learnedExactValue);
  }

  private final boolean checkChildrenForExact() {
    double lo = 2;
    double hi = -2;
    boolean hasNonExact = false;

    for (Node<P, M> child : children) {
      if (!child.knowExact) {
        hasNonExact = true;
        continue;
      }
      double v = child.payoff;
      if (v < lo)
        lo = v;
      if (v > hi)
        hi = v;
    }

    boolean player = position.getPlayerBool();

    if (hasNonExact) {
      if (player && hi > 0) {
        payoff = 1;
      } else if (!player && lo < 0) {
        payoff = -1;
      } else {
        return false;
      }
    } else {
      payoff = player ? hi : lo;
    }

    knowExact = true;
    return true;
  }

  private final boolean maybeSetExactValue(Node<P, M> updatedChild) {
    boolean player = position.getPlayerBool();

    if (updatedChild.knowExact &&
        (player && updatedChild.payoff > 0 ||
         !player && updatedChild.payoff < 0)) {
      knowExact = true;
      payoff = updatedChild.payoff;
      return true;
    }

    double lo = 2;
    double hi = -2;

    for (Node<P, M> child : children) {
      if (!child.knowExact)
        return false;
      double v = child.payoff;
      if (v < lo)
        lo = v;
      if (v > hi)
        hi = v;
    }

    payoff = player ? hi : lo;
    knowExact = true;
    return true;
  }
}
