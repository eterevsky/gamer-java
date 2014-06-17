package players;

import gamer.Game;
import gamer.GameState;

interface Evaluator<G extends Game> extends Cloneable {
  // This method must not modify the state.
  double evaluate(GameState<G> state);

  public Evaluator<G> clone();
}
