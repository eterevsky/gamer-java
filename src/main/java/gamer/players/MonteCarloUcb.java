package gamer.players;

import gamer.def.Game;
import gamer.def.Position;

public class MonteCarloUcb<P, M> extends GenericPlayer<P, M> {
  private static class Selector<P, M> extends BanditSelector<P, M> {
    public boolean shouldCreateChildren() {
      return true;
    }

    public LeafSelector<P, M> newChildSelector() {
      return new LeafSelector<P, M>();
    }
  }

  @Override
  protected Node<P, M> getRoot(P position) {
    return new Node<P, M>(
        null, position, null, new Selector<P, M>(), nodeContext);
  }
}
