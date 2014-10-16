package gamer.chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gamer.def.GameStatus;

import org.junit.Test;

public class TestChessState {

  @Test
  public void foolsMate() {
    ChessState state0 = new ChessState();
    ChessState state1 = state0.play(ChessMove.of("f2", "f3"));
    ChessState state2 = state1.play(ChessMove.of("e7", "e5"));
    ChessState state3 = state2.play(ChessMove.of("g2", "g4"));
    ChessState state4 = state3.play(ChessMove.of("d8", "h4"));
    assertTrue(state4.isTerminal());
    assertEquals(GameStatus.LOSS, state4.status());
  }
}
