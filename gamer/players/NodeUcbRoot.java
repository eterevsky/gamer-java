package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;

import java.util.ArrayList;
import java.util.List;

final class NodeUcbRoot<G extends Game> implements Node<G> {
  private long samples = 0;
  private long pendingSamples = 0;
  private double sumValue = 0.0;
  private final GameState<G> state;
  private final List<NodeShallow<G>> children;

  NodeUcbRoot(GameState<G> state) {
    assert !state.status().isTerminal();
    this.state = state;
    List<Move<G>> moves = state.getMoves();
    children = new ArrayList<>(moves.size());
    for (Move<G> move : moves) {
      GameState<G> newState = state.play(move);
      children.add(new NodeShallow<>(this, newState, move));
    }
  }

  public boolean isExplored() {
    return true;
  }

  public boolean knowExactValue() {
    return false;
  }

  public GameState<G> getState() {
    return state;
  }

  public Move<G> getMove() {
    throw new UnsupportedOperationException("root");
  }

  public double getValue() {
    return sumValue / samples;
  }

  public long getSamples() {
    return samples;
  }

  public List<? extends Node<G>> getChildren() {
    return children;
  }

  public void addPendingSamples(long nsamples) {
    pendingSamples += nsamples;
  }

  public void addSamplesToExact(long nsamples) {
    throw new UnsupportedOperationException("exact value not supported");
  }

  public void addSamples(long nsamples, double value) {
    assert value >= 0;
    assert value <= nsamples;

    samples += nsamples;
    pendingSamples -= nsamples;

    assert pendingSamples >= 0;
    assert sumValue >= 0;
    assert sumValue <= samples;
  }

  public void setExactValue(double value) {
    throw new UnsupportedOperationException("root");
  }

  public NodeShallow<G> selectChild() {
    double totalSamplesLog = 2 * Math.log(samples + pendingSamples);
    assert totalSamplesLog >= 0;

    NodeShallow<G> bestChild = null;
    double bestChildPrio = 0;
    for (NodeShallow<G> child : children) {
      double priority = child.getUcbPriority(totalSamplesLog);
      if (bestChild == null || priority > bestChildPrio) {
        bestChild = child;
        bestChildPrio = priority;
      }
    }

    return bestChild;
  }

  private boolean getPlayer() {
    return state.status().getPlayer();
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append('\n');
    builder.append(state.toString());
    builder.append(
        String.format(" %.1f/%d/%d", getValue(), samples, pendingSamples));
    for (NodeShallow<G> child : children) {
      builder.append("  ");
      builder.append(child.toString());
    }

    return builder.toString();
  }
}
