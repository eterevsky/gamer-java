package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;

public class MonteCarloUct<G extends Game> extends GenericPlayer<G> {
  @Override
  protected Node<G> getRoot(GameState<G> state) {
    return new NodeUct<G>(state);
  }
}
