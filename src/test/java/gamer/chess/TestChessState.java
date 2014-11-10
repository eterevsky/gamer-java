package gamer.chess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gamer.def.GameStatus;

import java.util.Arrays;
import java.util.List;

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
    ChessState state0 = Chess.getInstance().newGame();
    ChessState state1 = state0.play(ChessMove.of("f2", "f3"));
    ChessState state2 = state1.play(ChessMove.of("e7", "e5"));
    ChessState state3 = state2.play(ChessMove.of("g2", "g4"));
    ChessState state4 = state3.play(ChessMove.of("d8", "h4"));
    assertTrue(state4.isTerminal());
    assertEquals(GameStatus.LOSS, state4.status());
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
    assertEquals(GameStatus.WIN, state.status());
  }

}
