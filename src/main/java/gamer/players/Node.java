package gamer.players;

import gamer.def.Move;
import gamer.def.Position;
import gamer.def.Solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class Node<P extends Position<P, M>, M extends Move> {
  private final NodeContext<P, M> context;
  private final Node<P, M> parent;
  private List<Node<P, M>> children = null;
  private final P position;
  private final M move;
  private final Selector<P, M> selector;
  private boolean exact = false;
  // Exact if exact == true, mean otherwise.
  private double payoff;
  private int totalSamples = 0;
  private int pendingSamples = 0;

  // Used as a result of selectChildOrAddPending().
  @SuppressWarnings({ "rawtypes", "unchecked" })
  final static Node KNOW_EXACT_VALUE = new Node(null, null, null, null, null);

  interface Selector<P extends Position<P, M>, M extends Move> {
    void setNode(Node<P, M> node);

    Node<P, M> select(Collection<Node<P, M>> children, long totalSamples);

    boolean shouldCreateChildren();

    Selector<P, M> newChildSelector();
  }

  Node(Node<P, M> parent,
       P position,
       M move,
       Selector<P, M> selector,
       NodeContext<P, M> context) {
    this.context = context;
    this.parent = parent;
    this.position = position;
    this.move = move;
    this.selector = selector;
    if (selector != null) {
      selector.setNode(this);
    }

    if (position != null) {
      if (position.isTerminal()) {
        this.payoff = position.getPayoff(0);
        this.exact = true;
      } else if (context.solver != null) {
        Solver.Result<M> result = context.solver.solve(position);
        if (result != null) {
          this.payoff = result.payoff;
          this.exact = true;
        }
      }
    }
  }

  Node<P, M> getParent() {
    return parent;
  }

  P getPosition() {
    return position;
  }

  M getMove() {
    return move;
  }

  synchronized Collection<Node<P, M>> getChildren() {
    if (children == null) {
      throw new RuntimeException(
          "Requested children from a node without children.");
    }
    return children;
  }

  double getValue() {
    return payoff;
  }

  boolean knowExactValue() {
    return exact;
  }

  synchronized int getSamples() {
    return totalSamples - pendingSamples;
  }

  // Samples, including pending.
  int getTotalSamples() {
    return totalSamples;
  }

  // - If the exact value is known, add nsamples to samples (and parent samples)
  //   and return KNOW_EXACT_VALUE.
  // - If there are children, select one using Selector, add nsamples to pending
  //   samples.
  // - If there are no children, add nsamples to pending samples and return
  //   null.
  Node<P, M> selectChildOrAddPending(int nsamples) {
    @SuppressWarnings("unchecked")
    Node<P, M> returnValue = KNOW_EXACT_VALUE;
    boolean learnedExactValue = false;

    synchronized(this) {
      totalSamples += nsamples;
      if (!exact) {
        pendingSamples += nsamples;

        if (children == null && selector.shouldCreateChildren()) {
          learnedExactValue = initChildren();
        }

        if (!learnedExactValue) {
          if (children == null) {
            returnValue = null;
          } else {
            returnValue = selector.select(children, totalSamples);
          }
        }
      }
    }

    if (parent != null && returnValue == KNOW_EXACT_VALUE) {
      parent.addSamplesAndUpdate(
          nsamples, payoff, this, learnedExactValue);
    }

    return returnValue;
  }

  void addSamples(int nsamples, double value) {
    addSamplesAndUpdate(nsamples, value, null, false);
  }

  double getUcbPriority(double parentSamplesLog, boolean player) {
    if (totalSamples == 0) {
      return 2 * (1 + Math.sqrt(parentSamplesLog));
    }

    double value = getValue();
    return (player ? value : 1 - value) +
           Math.sqrt(parentSamplesLog / totalSamples);
  }

  Node<P, M> getChildByStateForTest(P position) {
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

  public String toString() {
    return toString(0);
  }

  private String toString(int indent) {
    StringBuilder builder = new StringBuilder();
    builder.append('\n');
    for (int i = 0; i < indent; i++) {
      builder.append(' ');
    }
    if (this == KNOW_EXACT_VALUE) {
      builder.append("KNOW_EXACT_VALUE");
      return builder.toString();
    }
    if (move != null) {
      builder.append(move.toString());
    } else {
      builder.append(position.toString());
    }
    builder.append(
        String.format(" %.1f/%d/%d", getValue(), totalSamples, pendingSamples));
    if (knowExactValue()) {
      builder.append(" exact");
    }
    if (children != null) {
      for (Node<P, M> child : children) {
        builder.append(child.toString(indent + 2));
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
        assert child.knowExactValue();
        learnedExactValue = maybeSetExactValue(child);
      }
    }

    if (parent != null)
      parent.addSamplesAndUpdate(nsamples, value, this, learnedExactValue);
  }

  private boolean initChildren() {
    List<M> moves = position.getMoves();
    children = new ArrayList<>(moves.size());
    for (M move : moves) {
      P newState = position.play(move);
      children.add(
          new Node<P, M>(
              this, newState, move, selector.newChildSelector(), context));
    }

    if (!context.propagateExact)
      return false;

    double lo = 2;
    double hi = -2;
    boolean hasNonExact = false;

    for (Node<P, M> child : children) {
      if (!child.exact) {
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

    exact = true;
    if (hasNonExact) {
      if (player && hi == 1) {
        payoff = 1;
      } else if (!player && lo == -1) {
        payoff = -1;
      } else {
        exact = false;
      }
    } else {
      payoff = player ? hi : lo;
    }

    return exact;
  }

  private boolean maybeSetExactValue(Node<P, M> updatedChild) {
    boolean player = position.getPlayerBool();

    if (updatedChild.exact &&
        (player && updatedChild.payoff == 1 ||
         !player && updatedChild.payoff == -1)) {
      exact = true;
      payoff = updatedChild.payoff;
      return true;
    }

    double lo = 2;
    double hi = -2;

    for (Node<P, M> child : children) {
      if (!child.exact)
        return false;
      double v = child.payoff;
      if (v < lo)
        lo = v;
      if (v > hi)
        hi = v;
    }

    payoff = player ? hi : lo;
    exact = true;
    return true;
  }
}
