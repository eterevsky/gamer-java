package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;

public class MonteCarloUcb<G extends Game> extends GenericPlayer<G> {
  @Override
  protected Node<G> getRoot(GameState<G> state) {
    return new NodeUcbRoot<G>(state);
  }
}
