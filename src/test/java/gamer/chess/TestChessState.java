package gamer.chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class TestChessState {

  @Test
  public void foolsMate() {
    ChessState state0 = Chess.getInstance().newGame();
    ChessState state1 = state0.play(ChessMove.of("f2", "f3"));
    ChessState state2 = state1.play(ChessMove.of("e7", "e5"));
    ChessState state3 = state2.play(ChessMove.of("g2", "g4"));
    ChessState state4 = state3.play(ChessMove.of("d8", "h4"));
    assertTrue(state4.isTerminal());
    assertEquals(-1, state4.getPayoff(0));
  }

  @Test
  public void scholarsMate() {
    List<String> moves = Arrays.asList(
        "e4", "e5", "Qh5", "Nc6", "Bc4", "Nf6", "Qxf7");

    ChessState state = Chess.getInstance().newGame();

    for (String move : moves) {
      state = state.play(move);
    }
    assertTrue(state.isTerminal());
    assertEquals(1, state.getPayoff(0));
  }

}
