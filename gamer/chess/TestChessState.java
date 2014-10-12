package gamer.chess;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestChessState {

  @Test
  public void simpleGame() {
    ChessState state0 = new ChessState();
    ChessState state1 = state0.play(ChessMove.of("e2", "e4"));
  }
}
