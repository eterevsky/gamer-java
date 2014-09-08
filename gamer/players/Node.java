package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class Node<G extends Game> {
  private final Node<G> parent;
  private List<Node<G>> children = null;
  private final GameState<G> state;
  private final Move<G> move;
  private final Selector<G> selector;
  private boolean exactValue = false;
  private double value = 0.5;
  private int totalSamples = 0;
  private int pendingSamples = 0;

  // Used as a result of selectChildOrAddPending().
  @SuppressWarnings("unchecked")
  final static Node KNOW_EXACT_VALUE = new Node(null, null, null, null);

  interface Selector<G extends Game> {
    void setNode(Node<G> node);

    Node<G> select(Collection<Node<G>> children, long totalSamples);

    boolean shouldCreateChildren();

    Selector<G> newChildSelector();

    void childUpdated(Node<G> child, long totalSamples);
  }

  Node(Node<G> parent, GameState<G> state, Move<G> move, Selector<G> selector) {
    this.parent = parent;
    this.state = state;
    this.move = move;
    this.selector = selector;
    if (selector != null) {
      selector.setNode(this);
    }

    if (state != null && state.status().isTerminal()) {
      this.exactValue = true;
      this.value = state.status().value();
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

  synchronized double getValue() {
    return value;
  }

  boolean knowExactValue() {
    return exactValue;
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

    synchronized(this) {
      totalSamples += nsamples;
      if (!exactValue) {
        pendingSamples += nsamples;
        value *= ((double)totalSamples - nsamples) / totalSamples;

        if (children == null && selector.shouldCreateChildren()) {
          initChildren();
        }

        returnValue =
            children != null ? selector.select(children, totalSamples) : null;
      }
    }

    if (parent != null) {
      if (returnValue == KNOW_EXACT_VALUE) {
        parent.addSamplesAndUpdate(nsamples, value, this);
      } else {
        parent.childUpdated(this);
      }
    }

    return returnValue;
  }

  void addSamples(int nsamples, double value) {
    addSamplesAndUpdate(nsamples, value, null);
  }

  // Ideally, this should be synchronized.
  double getUcbPriority(double parentSamplesLog, boolean player) {
    if (totalSamples == 0) {
      return 2 * (1 + Math.sqrt(parentSamplesLog));
    }

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
    if (move != null) {
      builder.append(move.toString());
    } else {
      builder.append(state.toString());
    }
    builder.append(
        String.format(" %.1f/%d/%d", value, totalSamples, pendingSamples));
    if (children != null) {
      for (Node<G> child : children) {
        builder.append(child.toString(indent + 2));
      }
    }

    return builder.toString();
  }

  private void addSamplesAndUpdate(int nsamples, double value, Node<G> child) {
    synchronized(this) {
      pendingSamples -= nsamples;
      assert pendingSamples >= 0;
      if (!exactValue)
        this.value += value * (double)nsamples / totalSamples;
      if (child != null)
        selector.childUpdated(child, totalSamples);
    }

    if (parent != null)
      parent.addSamplesAndUpdate(nsamples, value, this);
  }

  private synchronized void childUpdated(Node<G> child) {
    selector.childUpdated(child, totalSamples);
  }


  private void initChildren() {
    List<Move<G>> moves = state.getMoves();
    children = new ArrayList<>(moves.size());
    for (Move<G> move : moves) {
      GameState<G> newState = state.play(move);
      children.add(
          new Node<G>(this, newState, move, selector.newChildSelector()));
    }
  }
}
