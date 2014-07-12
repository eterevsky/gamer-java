package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;

import java.util.Random;

interface Evaluator<G extends Game> extends Cloneable {
  // This method must not modify the state.
  double evaluate(GameState<G> state);

  public Evaluator<G> clone();
}
