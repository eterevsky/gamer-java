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
  private long samples = 0;
  private long pendingSamples = 0;

  interface Selector<G extends Game> {
    void setNode(Node<G> node);

    Node<G> select(Collection<Node<G>> children,
                   long samples,
                   long pendingSamples);

    boolean shouldCreateChildren();

    Selector<G> newChildSelector();

    void childUpdated(Node<G> child, long totalSamples);
  }

  Node(Node<G> parent, GameState<G> state, Move<G> move, Selector<G> selector) {
    this.parent = parent;
    this.state = state;
    this.move = move;
    this.selector = selector;
    selector.setNode(this);
  }

  Node<G> getParent() {
    return parent;
  }

  synchronized Collection<Node<G>> getChildren() {
    if (children == null) {
      initChildren();
    }
    return children;
  }

  /**
   * If there are children: return false, do nothing.
   * If there are no children: return true, add nsamples to pending samples.
   */
  boolean selectIfNoChildren(int nsamples) {
    synchronized(this) {
      if (!exactValue &&
          (children != null || selector.shouldCreateChildren())) {
        return false;
      }

      pendingSamples += nsamples;
    }

    if (parent != null)
      parent.childUpdated(this);

    return true;
  }

  synchronized Node<G> selectChild(int nsamples) {
    if (selector == null) {
      throw new RuntimeException("no selector");
    }

    if (exactValue) {
      System.out.println("exact");
      samples += nsamples;
      return null;
    }

    if (children == null) {
      initChildren();
    }

    pendingSamples += nsamples;
    return selector.select(children, samples, pendingSamples);
  }

  double getValue() {
    return value;
  }

  boolean knowExactValue() {
    return exactValue;
  }

  synchronized void setExactValue(double value) {
    this.value = value;
    this.exactValue = true;
  }

  synchronized long getSamples() {
    return samples;
  }

  synchronized long getSamplesWithPending() {
    return samples + pendingSamples;
  }

  void addSamples(long nsamples, double value) {
    synchronized(this) {
      if (pendingSamples < nsamples) {
        System.out.println();
        System.out.println(toString());
      }
      long newSamples = samples + nsamples;
      this.value = (samples * this.value + nsamples * value) / newSamples;
      samples = newSamples;
      pendingSamples -= nsamples;
      assert pendingSamples >= 0;
    }
    if (parent != null)
      parent.childUpdated(this);
  }

  synchronized double getUcbPriority(double parentSamplesLog, boolean player) {
    long totalSamples = samples + pendingSamples;
    if (totalSamples == 0) {
      return 2 * (1 + Math.sqrt(parentSamplesLog));
    }

    return (player ? value : 1 - value) * samples / totalSamples +
           Math.sqrt(parentSamplesLog / totalSamples);
  }

  GameState<G> getState() {
    return state;
  }

  Move<G> getMove() {
    return move;
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
        String.format(" %.1f/%d/%d", value, samples, pendingSamples));
    if (children != null) {
      for (Node<G> child : children) {
        builder.append(child.toString(indent + 2));
      }
    }

    return builder.toString();
  }

  private synchronized void childUpdated(Node<G> child) {
    selector.childUpdated(child, samples + pendingSamples);
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
