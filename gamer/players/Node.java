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
  final static Node<G> KNOW_EXACT_VALUE = new Node(null, null, null, null);

  interface Selector<G extends Game> {
    void setNode(Node<G> node);

    Node<G> select(Collection<Node<G>> children, long totalSamples);

    boolean shouldCreateChildren();

    Selector<G> newChildSelector();

    void childUpdated(Node<G> child, long totalSamples);
  }

  Node(Node<G> parent, GameState<G> state, Move<G> move, Selector<G> selector) {
    if (selector == null) {
      throw new RuntimeException("no selector");
    }

    this.parent = parent;
    this.state = state;
    this.move = move;
    this.selector = selector;
    selector.setNode(this);
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
      throw RuntimeException(
          "Requested children from a node without children.");
    }
    return children;
  }

  double getValue() {
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

  // - If there are children, select one using Selector, add nsamples to pending
  //   samples.
  // - If there are no children and
  Node<G> selectChildOrAddPending(int nsamples) {
  }

  /**
   * If there are children: return false, do nothing.
   * If there are no children: return true, add nsamples to pending samples.
   */
  boolean addPendinIfNoChildren(int nsamples) {
    synchronized(this) {
      if (!exactValue) {
        if (children != null || selector.shouldCreateChildren()) {
          return false;
        }

        value *= totalSamples / (totalSamples + nsamples);
        pendingSamples += nsamples;
        totalSamples += nsamples;
      } else {
        pendingSamples += nsamples;
        totalSamples += nsamples;
      }
    }

    if (parent != null)
      parent.childUpdated(this);

    return true;
  }

  synchronized Node<G> selectChild(int nsamples) {
    if (exactValue) {
      System.out.println("exact");
      totalSamples += nsamples;
      return null;
    }

    if (children == null) {
      initChildren();
    }

    value *= totalSamples / (totalSamples + nsamples);
    pendingSamples += nsamples;
    totalSamples += nsamples;
    return selector.select(children, totalSamples);
  }

  synchronized void setExactValue(double value) {
    this.value = value;
    this.exactValue = true;
  }

  void addSamples(int nsamples, double value) {
    assert nsamples <= pendingSamples;

    synchronized(this) {
      this.value += value * nsamples / totalSamples;
      // System.out.format("%s: + %f (%d) new value: %f\n", state, value, nsamples, this.value);
      // System.out.println(totalSamples);
      // System.out.println(pendingSamples);
      pendingSamples -= nsamples;
      assert pendingSamples >= 0;
    }

    if (parent != null)
      parent.childUpdated(this);
  }

  // Ideally, this should be synchronized.
  double getUcbPriority(double parentSamplesLog, boolean player) {
    if (totalSamples == 0) {
      return 2 * (1 + Math.sqrt(parentSamplesLog));
    }

    return (player ? value : 1 - value) +
           Math.sqrt(parentSamplesLog / totalSamples);
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
