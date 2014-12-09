package gamer.players;

import gamer.def.Move;
import gamer.def.Position;
import gamer.def.Solver;

final class NodeContext<P extends Position<P, M>, M extends Move> {
  boolean propagateExact = true;
  Solver<P, M> solver = null;
  int childrenThreshold = 0;

  NodeContext() {}

  NodeContext(boolean propagateExact, Solver<P, M> solver) {
    this.propagateExact = propagateExact;
    this.solver = solver;
  }
}
