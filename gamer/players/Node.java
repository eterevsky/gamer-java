package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class Node<G extends Game> {
  private final NodeContext context;
  private final Node<G> parent;
  private List<Node<G>> children = null;
  private final GameState<G> state;
  private final Move<G> move;
  private final Selector<G> selector;
  // 0 - LOSS
  // 1 - DRAW
  // 2 - WIN
  // 3 - DON'T KNOW
  private int exactValue = 3;
  private double value = 0.5;
  private int totalSamples = 0;
  private int pendingSamples = 0;

  // Used as a result of selectChildOrAddPending().
  @SuppressWarnings("unchecked")
  final static Node KNOW_EXACT_VALUE = new Node(null, null, null, null, null);

  interface Selector<G extends Game> {
    void setNode(Node<G> node);

    Node<G> select(Collection<Node<G>> children, long totalSamples);

    boolean shouldCreateChildren();

    Selector<G> newChildSelector();
  }

  Node(Node<G> parent,
       GameState<G> state,
       Move<G> move,
       Selector<G> selector,
       NodeContext context) {
    this.context = context;
    this.parent = parent;
    this.state = state;
    this.move = move;
    this.selector = selector;
    if (selector != null) {
      selector.setNode(this);
    }

    if (state != null) {
      this.exactValue = state.status().valueInt();
    }
  }

  // Getters

  Node<G> getParent() {
    return parent;
  }

  GameState<G> getState() {
    return state;
  }

  Move<G> getMove() {
    return move;
  }

  synchronized Collection<Node<G>> getChildren() {
    if (children == null) {
      throw new RuntimeException(
          "Requested children from a node without children.");
    }
    return children;
  }

  double getValue() {
    if (exactValue == 3) {
      return value;
    } else {
      return exactValue / 2.0;
    }
  }

  boolean knowExactValue() {
    return exactValue != 3;
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
  Node<G> selectChildOrAddPending(int nsamples) {
    @SuppressWarnings("unchecked")
    Node<G> returnValue = KNOW_EXACT_VALUE;
    boolean learnedExactValue = false;

    synchronized(this) {
      totalSamples += nsamples;
      if (!knowExactValue()) {
        pendingSamples += nsamples;
        value *= ((double)totalSamples - nsamples) / totalSamples;

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
          nsamples, exactValue / 2.0, this, learnedExactValue);
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

  Node<G> getChildByStateForTest(GameState<G> state) {
    if (children == null) {
      throw new RuntimeException(
          "Requested children from a node without children.");
    }
    for (Node<G> child : children) {
      if (child.state.equals(state))
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
      builder.append(state.toString());
    }
    builder.append(
        String.format(" %.1f/%d/%d", getValue(), totalSamples, pendingSamples));
    if (knowExactValue()) {
      builder.append(" exact");
    }
    if (children != null) {
      for (Node<G> child : children) {
        builder.append(child.toString(indent + 2));
      }
    }

    return builder.toString();
  }

  private void addSamplesAndUpdate(
      int nsamples, double value, Node<G> child,
      boolean childLearnedExactValue) {
    boolean learnedExactValue = false;

    synchronized(this) {
      pendingSamples -= nsamples;
      assert pendingSamples >= 0;
      this.value += value * (double)nsamples / totalSamples;

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
    List<Move<G>> moves = state.getMoves();
    children = new ArrayList<>(moves.size());
    for (Move<G> move : moves) {
      GameState<G> newState = state.play(move);
      children.add(
          new Node<G>(
              this, newState, move, selector.newChildSelector(), context));
    }

    int lo = 2;
    int hi = 0;
    boolean hasNonExact = false;

    for (Node<G> child : children) {
      int v = child.exactValue;
      if (v == 3)
        hasNonExact = true;
      if (v < lo)
        lo = v;
      if (v > hi)
        hi = v;
    }

    boolean player = state.status().getPlayer();

    if (hasNonExact) {
      if (player && hi == 2) {
        exactValue = 2;
      } else if (!player && lo == 0) {
        exactValue = 0;
      } else {
        exactValue = 3;
      }
    } else {
      exactValue = player ? hi : lo;
    }

    return exactValue != 3;
  }

  private boolean maybeSetExactValue(Node<G> updatedChild) {
    boolean player = state.status().getPlayer();

    if (player && updatedChild.exactValue == 2 ||
        !player && updatedChild.exactValue == 0) {
      exactValue = updatedChild.exactValue;
      return true;
    }

    int lo = 2;
    int hi = 0;

    for (Node<G> child : children) {
      int v = child.exactValue;
      if (v == 3)
        return false;
      if (v < lo)
        lo = v;
      if (v > hi)
        hi = v;
    }

    exactValue = player ? hi : lo;
    return true;
  }
}
