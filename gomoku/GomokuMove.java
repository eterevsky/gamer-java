package gomoku;

import gamer.Move;

public class GomokuMove implements Move<Gomoku> {
  /* package */ int cell;
  /* package */ boolean player;

  /* package */ GomokuMove(int cell, boolean firstPlayerMoves) {
    this.cell = cell;
    this.player = player;
  }
}
