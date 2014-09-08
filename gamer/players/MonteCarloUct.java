package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;

public class MonteCarloUct<G extends Game> extends GenericPlayer<G> {
  private static class Selector<G extends Game>
      extends BanditSelector<G> {
    public boolean shouldCreateChildren() {
      return node.getSamples() > 4;
    }

    public Selector<G> newChildSelector() {
      return new Selector<G>();
    }
  }

  @Override
  protected Node<G> getRoot(GameState<G> state) {
    return new Node<G>(null, state, null, new Selector<G>());
  }
}
