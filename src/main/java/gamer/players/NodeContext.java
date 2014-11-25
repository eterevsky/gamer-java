package gamer.players;

import gamer.def.Position;
import gamer.def.Solver;

final class NodeContext<P extends Position<?,?>> {
  final boolean propagateExact;
  final Solver<P> solver;

  NodeContext() {
    propagateExact = true;
    helper = null;
  }

  NodeContext(boolean propagateExact, Solver<P> solver) {
    this.propagateExact = propagateExact;
    this.solver = solver;
  }
}
