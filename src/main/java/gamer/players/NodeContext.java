package gamer.players;

import gamer.def.Game;
import gamer.def.Move;
import gamer.def.Position;
import gamer.def.Solver;

final class NodeContext<P extends Position<P, M>, M extends Move> {
  boolean propagateExact = true;
  Solver<P, M> solver = null;
  int childrenThreshold = 1;
  // max_payoff - min_payoff
  int payoffSpread = 2;
  Game<P, M> game = null;

  NodeContext() {}

  NodeContext(boolean propagateExact, Solver<P, M> solver, Game<P, M> game) {
    this.propagateExact = propagateExact;
    this.solver = solver;
    this.game = game;
    this.payoffSpread = game.getMaxPayoff() - game.getMinPayoff();
  }
}
