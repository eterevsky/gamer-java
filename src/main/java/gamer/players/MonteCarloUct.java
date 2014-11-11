package gamer.players;

import gamer.chess.Chess;
import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;
import gamer.gomoku.Gomoku;

public class MonteCarloUct<G extends Game> extends GenericPlayer<G> {
  private int childrenThreshold = -1;

  private class Selector<G extends Game> extends BanditSelector<G> {
    public boolean shouldCreateChildren() {
      return node.getSamples() > childrenThreshold;
    }

    public Selector<G> newChildSelector() {
      return new Selector<G>();
    }
  }

  @Override
  protected Node<G> getRoot(GameState<G> state) {
    return new Node<G>(null, state, null, new Selector<G>(), nodeContext);
  }

  public MonteCarloUct<G> setChildrenThreshold(int threshold) {
    childrenThreshold = threshold;
    return this;
  }

  public String getName() {
    if (name != null)
      return name;
    if (childrenThreshold == -1)
      childrenThreshold = samplesBatch;
    return super.getName() + String.format(" ct%d", childrenThreshold);
  }

  @Override
  public Move<G> selectMove(GameState<G> state) {
    if (childrenThreshold == -1)
      childrenThreshold = samplesBatch;
    return super.selectMove(state);
  }
}
