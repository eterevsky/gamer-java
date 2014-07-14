package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;

import java.util.ArrayList;
import java.util.List;

final class NodeUct<G extends Game> implements Node<G> {
  private long samples = 0;
  private long pendingSamples = 0;
  private double exactValue = -1.0;
  private double sumValue = 0.0;
  private final GameState<G> state;
  private final NodeUct<G> parent;  // null for root
  private final Move<G> lastMove;  // null for root
  private List<NodeUct<G>> children = null;

  NodeUct(GameState<G> state) {
    this(null, state, null);
  }

  private NodeUct(NodeUct<G> parent, GameState<G> state, Move<G> lastMove) {
    this.parent = parent;
    this.state = state;
    this.lastMove = lastMove;
  }

  public boolean isExplored() {
    return samples + pendingSamples > 2;
  }

  public boolean knowExactValue() {
    return exactValue >= 0.0;
  }

  public GameState<G> getState() {
    return state;
  }

  public Move<G> getMove() {
    if (lastMove == null) {
      throw new RuntimeException("no last move");
    }
    return lastMove;
  }

  public double getValue() {
    return exactValue >= 0 ? exactValue : sumValue / samples;
  }

  public long getSamples() {
    return samples;
  }

  public List<? extends Node<G>> getChildren() {
    if (children == null) {
      throw new RuntimeException("no children");
    }
    return children;
  }

  public void addPendingSamples(long nsamples) {
    pendingSamples += nsamples;
  }

  public void addSamplesToExact(long nsamples) {
    samples += nsamples;
    double value = getPlayer() ? exactValue : 1.0 - exactValue;
    parent.addSamples(nsamples, nsamples * value);
  }

  public void addSamples(long nsamples, double value) {
    assert value >= 0;
    assert value <= nsamples;
    samples += nsamples;
    pendingSamples -= nsamples;
    assert pendingSamples >= 0;

    sumValue += getPlayer() ? value : nsamples - value;
    if (parent != null)
      parent.addSamples(nsamples, value);

    assert sumValue >= 0;
    assert sumValue <= samples;
  }

  public void setExactValue(double value) {
    exactValue = getPlayer() ? value : 1.0 - value;
  }

  public NodeUct<G> selectChild() {
    if (children == null)
      initChildren();

    double totalSamplesLog = 2 * Math.log(samples + pendingSamples);
    assert totalSamplesLog >= 0;

    NodeUct<G> bestChild = null;
    double bestChildPrio = 0;
    for (NodeUct<G> child : children) {
      if (!child.isExplored())
        return child;

      double priority = child.getPriority(totalSamplesLog);
      if (bestChild == null || priority > bestChildPrio) {
        bestChild = child;
        bestChildPrio = priority;
      }
    }

    return bestChild;
  }

  public String toString() {
    return toString(0);
  }

  private boolean getPlayer() {
    if (!state.status().isTerminal()) {
      return state.status().getPlayer();
    } else {
      return !parent.getState().status().getPlayer();
    }
  }

  private String toString(int indent) {
    StringBuilder builder = new StringBuilder();
    builder.append('\n');
    for (int i = 0; i < indent; i++) {
      builder.append(' ');
    }
    if (lastMove != null) {
      builder.append(lastMove.toString());
    } else {
      builder.append(state.toString());
    }
    builder.append(
        String.format(" %.1f/%d/%d", getValue(), samples, pendingSamples));
    if (children != null) {
      for (NodeUct<G> child : children) {
        builder.append(child.toString(indent + 2));
      }
    }

    return builder.toString();
  }

  private void initChildren() {
    List<Move<G>> moves = state.getMoves();
    children = new ArrayList<>(moves.size());
    for (Move<G> move : moves) {
      GameState<G> newState = state.play(move);
      children.add(new NodeUct<>(this, newState, move));
    }
  }

  private double getPriority(double parentSamplesLog) {
    long totalSamples = samples + pendingSamples;
    assert totalSamples > 0;

    double value = exactValue >= 0 ? 1 - exactValue
                                   : (samples - sumValue) / totalSamples;

    return value + Math.sqrt(parentSamplesLog / totalSamples);
  }
}
