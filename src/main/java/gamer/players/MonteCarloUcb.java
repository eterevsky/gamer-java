package gamer.players;

import gamer.def.Move;
import gamer.def.Position;

public class MonteCarloUcb<P extends Position<P, M>, M extends Move>
    extends GenericPlayer<P, M> {
  private static class Selector<P extends Position<P, M>, M extends Move>
      extends BanditSelector<P, M> {
    @Override
    public boolean shouldCreateChildren() {
      return true;
    }

    @Override
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
