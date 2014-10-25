package gamer.players;

import gamer.def.Game;
import gamer.def.Helper;

final class NodeContext<G extends Game> {
  final boolean propagateExact;
  final Helper<G> helper;

  NodeContext() {
    propagateExact = false;
    helper = null;
  }

  NodeContext(boolean propagateExact, Helper<G> helper) {
    this.propagateExact = propagateExact;
    this.helper = helper;
  }
}
