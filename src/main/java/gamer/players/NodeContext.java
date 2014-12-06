package gamer.players;

import gamer.def.Move;
import gamer.def.Position;
import gamer.def.Solver;

final class NodeContext<P extends Position<P, M>, M extends Move> {
  final boolean propagateExact;
  final Solver<P, M> solver;

  NodeContext() {
    propagateExact = true;
    solver = null;
  }

  NodeContext(boolean propagateExact, Solver<P, M> solver) {
    this.propagateExact = propagateExact;
    this.solver = solver;
  }
}
