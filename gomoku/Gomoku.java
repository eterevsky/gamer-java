package gomoku;

import gamer.Game;

public class Gomoku implements Game<Gomoku> {
  static final int SIZE = 19;

  public static GomokuState newGame() {
    return new GomokuState();
  }
}
