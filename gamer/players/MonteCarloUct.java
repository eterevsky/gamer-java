package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;

public class MonteCarloUct<G extends Game> extends GenericPlayer<G> {
  private int childrenThreshold = 4;

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
    return new Node<G>(null, state, null, new Selector<G>());
  }

  public MonteCarloUct<G> setChildrenThreshold(int threshold) {
    childrenThreshold = threshold;
    return this;
  }

  public String getName() {
    if (name != null)
      return name;
    return super.getName() + String.format(" ct%d", childrenThreshold);
  }
}
