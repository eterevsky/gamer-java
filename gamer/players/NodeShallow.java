package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;

import java.util.List;

final class NodeShallow<G extends Game> implements Node<G> {
  private long samples = 0;
  private long pendingSamples = 0;
  private double exactValue = -1.0;
  private double sumValue = 0.0;
  private final GameState<G> state;
  private final Node<G> parent;
  private final Move<G> lastMove;

  NodeShallow(Node<G> parent, GameState<G> state, Move<G> lastMove) {
    this.parent = parent;
    this.state = state;
    this.lastMove = lastMove;
  }

  public boolean isExplored() {
    return false;
  }

  public boolean knowExactValue() {
    return exactValue >= 0.0;
  }

  public GameState<G> getState() {
    return state;
  }

  public Move<G> getMove() {
    return lastMove;
  }

  public double getValue() {
    return exactValue >= 0 ? exactValue : sumValue / samples;
  }

  public long getSamples() {
    return samples;
  }

  public List<? extends Node<G>> getChildren() {
    throw new UnsupportedOperationException("shallow node");
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
    parent.addSamples(nsamples, value);

    assert sumValue >= 0;
    assert sumValue <= samples;
  }

  public void setExactValue(double value) {
    exactValue = getPlayer() ? value : 1.0 - value;
  }

  public NodeUct<G> selectChild() {
    throw new UnsupportedOperationException("root");
  }

  public String toString() {
    return String.format("%s: %.1f/%d/%d",
        lastMove.toString(), getValue(), samples, pendingSamples);
  }

  double getUcbPriority(double parentSamplesLog) {
    long totalSamples = samples + pendingSamples;
    if (totalSamples == 0) {
      return 2 * (1 + Math.sqrt(parentSamplesLog));
    }

    double value = exactValue >= 0 ? 1 - exactValue
                                   : (samples - sumValue) / totalSamples;

    return value + Math.sqrt(parentSamplesLog / totalSamples);
  }

  private boolean getPlayer() {
    if (!state.status().isTerminal()) {
      return state.status().getPlayer();
    } else {
      return !parent.getState().status().getPlayer();
    }
  }
}
