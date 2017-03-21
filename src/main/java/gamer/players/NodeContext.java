package gamer.players;

import gamer.def.Game;
import gamer.def.Move;
import gamer.def.State;
import gamer.def.Solver;

final class NodeContext<S extends State<S, M>, M extends Move> {
  boolean propagateExact = true;
  Solver<S, M> solver = null;
  int childrenThreshold = 1;
  // max_payoff - min_payoff
  int payoffSpread = 2;
  Game<S, M> game = null;

  NodeContext() {}

  NodeContext(boolean propagateExact, Solver<S, M> solver, Game<S, M> game) {
    this.propagateExact = propagateExact;
    this.solver = solver;
    this.game = game;
    this.payoffSpread = game.getMaxPayoff() - game.getMinPayoff();
  }
}
