package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;

import java.util.List;

interface Node<G extends Game> {
  // True = has children nodes.
  boolean isExplored();

  boolean knowExactValue();

  GameState<G> getState();

  Move<G> getMove();

  double getValue();

  long getSamples();

  List<? extends Node<G>> getChildren();

  void addPendingSamples(long nsamples);

  void addSamplesToExact(long nsamples);

  void addSamples(long nsamples, double value);

  void setExactValue(double value);

  Node<G> selectChild();
}
