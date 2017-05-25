package gamer.minimax;

import gamer.def.Evaluator;
import gamer.def.State;

// Trivial evaluator, returning 0 for each non-terminal state and payoff value
// otherwise.
public class TerminalEvaluator<S extends State<S, ?>> implements Evaluator<S> {
  public double evaluate(S state) {
    return state.isTerminal() ? state.getPayoff(0) : 0;
  }
}
