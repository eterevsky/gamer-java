package gamer.minimax;

import gamer.chess.ChessMove;
import gamer.chess.ChessSimpleEvaluator;
import gamer.chess.ChessState;
import org.junit.Test;

import static org.hamcrest.Matchers.isOneOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MinimaxChessTest {
  @Test(timeout = 500)
  public void lastMoveBeforeMate() {
    MinimaxPlayer<ChessState, ChessMove> player = new MinimaxPlayer<>();
    player.setEvaluator(ChessSimpleEvaluator.getInstance());

    // · n b q k b n r
    // r · p . p B p p
    // p p · · · · · ·
    // · · · · · · · Q
    // · · · · P · · ·
    // · · · · · · · ·
    // P P P P · P P P
    // R N B · K · N R
    ChessState state1 = ChessState
        .fromFen("1nbqkbnr/r1p1pBpp/pp6/7Q/4P3/8/PPPP1PPP/RNB1K1NR b KQ - 0 1");
    MinimaxPlayer.SearchResult<ChessMove> result =
        player.search(state1, 2, -1.0, 1.0);
    assertNotNull(result);
    assertEquals(state1.parseMove("Kd7"), result.move);
    assertTrue(
        String.format("Expected score around 1.0, but got %f", result.score),
        result.score > 0.9);
  }

  @Test(timeout=200)
  public void moveInOne() {
    MinimaxPlayer<ChessState, ChessMove> player = new MinimaxPlayer<>();
    player.setMaxDepth(4);
    player.setEvaluator(ChessSimpleEvaluator.getInstance());

    // · n b q k b n r
    // r · p . p p p p
    // p p · · · · · ·
    // · · · B · · · Q
    // · · · · P · · ·
    // · · · · · · · ·
    // P P P P · P P P
    // R N B · K · N R
    ChessState state2 = ChessState.fromFen(
        "1nbqkbnr/r1p1pppp/pp6/3B3Q/4P3/8/PPPP1PPP/RNB1K1NR w KQk - 0 1");
    ChessMove move = player.selectMove(state2);
    assertEquals(state2.parseMove("Bxf7"), move);
  }

  @Test(timeout=500)
  public void preventMate() {
    MinimaxPlayer<ChessState, ChessMove> player = new MinimaxPlayer<>();
    player.setEvaluator(ChessSimpleEvaluator.getInstance());
    player.setMaxDepth(4);

    // r n b q k b n r
    // · · p p p p p p
    // p p · · · · · ·
    // · · · · · · · ·
    // · · B · P · · ·
    // · · · · · Q · ·
    // P P P P · P P P
    // R N B · K · N R
    ChessState state3 = ChessState.fromFen(
        "rnbqkbnr/2pppppp/pp6/8/2B1P3/5Q2/PPPP1PPP/RNB1K1NR b KQkq - 1 3");
    ChessMove move = player.selectMove(state3);
    String moveStr = state3.moveToString(move);
    assertThat(String.format(
        "Expected a move that would prevent checkmate, got: %s", moveStr),
               moveStr, isOneOf("d5", "e6", "f6", "f5", "Nf6"));
  }

  @Test(timeout=1000)
  public void preventMateWithTimeout() {
    MinimaxPlayer<ChessState, ChessMove> player = new MinimaxPlayer<>();
    player.setEvaluator(ChessSimpleEvaluator.getInstance());
    player.setTimeout(300);

    // · n b q k b n r
    // r p p p p p p p
    // p · · · · · · ·
    // · · · · · · · Q
    // · · B · P · · ·
    // · · · · · · · ·
    // P P P P · P P P
    // R N B · K · N R
    ChessState state4 = ChessState.fromFen(
        "1nbqkbnr/rppppppp/p7/7Q/2B1P3/8/PPPP1PPP/RNB1K1NR b KQk - 3 3");
    ChessMove move = player.selectMove(state4);
    String moveStr = state4.moveToString(move);
    assertThat(String.format(
        "Expected a move that would prevent checkmate, got: %s", moveStr),
               moveStr, isOneOf("d5", "d6", "e6", "g6"));
  }
}
