package gamer;

import gamer.def.State;

public class GamerState {
  GamerState(State<?, ?> state, String report) {
    this.state = state;
    this.report = report;
  }

  State<?, ?> state = null;
  String report;
}
