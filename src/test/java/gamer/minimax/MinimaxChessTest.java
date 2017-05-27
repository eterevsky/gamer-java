package gamer.minimax;

import gamer.chess.ChessMove;
import gamer.chess.ChessSimpleEvaluator;
import gamer.chess.ChessState;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MinimaxChessTest {
  @Test(timeout=5000)
  public void simpleMate() {
    // · n b q k b n r
    // r · p . p B p p
    // p p · · · · · ·
    // · · · · · · · Q
    // · · · · P · · ·
    // · · · · · · · ·
    // P P P P · P P P
    // R N B · K · N R
    ChessState state1 = ChessState.fromFen("1nbqkbnr/r1p1pBpp/pp6/7Q/4P3/8/PPPP1PPP/RNB1K1NR b KQ - 0 1");
    MinimaxPlayer<ChessState, ChessMove> player = new MinimaxPlayer<>();
    player.setMaxDepth(2);
    player.setEvaluator(ChessSimpleEvaluator.getInstance());
    MinimaxPlayer.SearchResult<ChessMove> result = player.search(state1, 2, -1.0, 1.0);
    assertNotNull(result);
    assertEquals(state1.parseMove("Kd7"), result.move);
    assertTrue(String.format("Expected score around 1.0, but got %f", result.score), result.score > 0.9);

    // · n b q k b n r
    // r · p . p p p p
    // p p · · · · · ·
    // · · · B · · · Q
    // · · · · P · · ·
    // · · · · · · · ·
    // P P P P · P P P
    // R N B · K · N R
    ChessState state2 = ChessState.fromFen("1nbqkbnr/r1p1pppp/pp6/3B3Q/4P3/8/PPPP1PPP/RNB1K1NR w KQk - 0 1");
    player.setMaxDepth(4);
    ChessMove move = player.selectMove(state2);
    assertEquals(state2.parseMove("Bxf7"), move);
  }
}
