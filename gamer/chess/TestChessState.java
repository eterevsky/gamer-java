package gamer.chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gamer.def.GameStatus;

import org.junit.Test;

public class TestChessState {

  private void showMoves(ChessState state) {
    System.out.format("Moves:");
    for (ChessMove move : state.getMoves()) {
      System.out.format(" %s", state.moveToString(move));
    }
    System.out.println();
  }

  @Test
  public void foolsMate() {
    ChessState state0 = new ChessState();
    System.out.println();
    System.out.println(state0);
    showMoves(state0);

    ChessState state1 = state0.play(ChessMove.of("f2", "f3"));
    System.out.println();
    System.out.println(state1);
    showMoves(state1);

    ChessState state2 = state1.play(ChessMove.of("e7", "e5"));
    System.out.println();
    System.out.println(state2);
    showMoves(state2);

    ChessState state3 = state2.play(ChessMove.of("g2", "g4"));
    System.out.println();
    System.out.println(state3);
    showMoves(state3);

    ChessState state4 = state3.play(ChessMove.of("d8", "h4"));
    System.out.println();
    System.out.println(state4);
    assertTrue(state4.isTerminal());
    assertEquals(GameStatus.LOSS, state4.status());
  }

}
