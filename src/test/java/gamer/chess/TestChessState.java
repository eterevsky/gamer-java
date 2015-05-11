package gamer.chess;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestChessState {

  @Test
  public void foolsMate() {
    ChessState state = Chess.getInstance().newGame();
    state.play(ChessMove.of("f2", "f3"));
    state.play(ChessMove.of("e7", "e5"));
    state.play(ChessMove.of("g2", "g4"));
    state.play(ChessMove.of("d8", "h4"));
    assertTrue(state.isTerminal());
    assertEquals(-1, state.getPayoff(0));
  }

  @Test
  public void scholarsMate() {
    List<String> moves = Arrays.asList(
        "e4", "e5", "Qh5", "Nc6", "Bc4", "Nf6", "Qxf7");

    ChessState state = Chess.getInstance().newGame();

    for (String move : moves) {
      state.play(move);
    }
    assertTrue(state.isTerminal());
    assertEquals(1, state.getPayoff(0));
  }

  @Test
  public void cloneIsIndependent() {
    ChessState state = Chess.getInstance().newGame();
    ChessState stateClone = state.clone();
    state.play("e4");
    assertEquals(Pieces.EMPTY, state.get("e2"));
    assertEquals(Pieces.white(Pieces.PAWN), state.get("e4"));
    assertEquals(Pieces.white(Pieces.PAWN), stateClone.get("e2"));
    assertEquals(Pieces.EMPTY, stateClone.get("e4"));
    stateClone.play("e4");
  }
}
