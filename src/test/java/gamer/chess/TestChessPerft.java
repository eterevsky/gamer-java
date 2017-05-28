package gamer.chess;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestChessPerft {
  int castlings = 0;
  private final static ChessState INITIAL = ChessState.fromFen(
      "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
  private final static ChessState KIWIPETE = ChessState.fromFen(
      "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
  private final static ChessState POSITION3 = ChessState.fromFen(
      "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
  private final static ChessState POSITION4 = ChessState.fromFen(
      "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
  private final static ChessState POSITION4_MIRROR = ChessState.fromFen(
      "r2q1rk1/pP1p2pp/Q4n2/bbp1p3/Np6/1B3NBn/pPPP1PPP/R3K2R b KQ - 0 1");
  private final static ChessState POSITION5 = ChessState.fromFen(
      "rnbqkb1r/pp1p1ppp/2p5/4P3/2B5/8/PPP1NnPP/RNBQK2R w KQkq - 0 6");
  private final static ChessState POSITION6 = ChessState.fromFen(
      "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");

  private long perft(ChessState state, int depth) {
    if (depth == 1)
      return state.getMoves().size();

    long total = 0;
    for (ChessMove move : state.getMoves()) {
      if (Pieces.piece(state.get(move.from)) == Pieces.KING &&
          Math.abs(move.from - move.to) == 16) {
        castlings++;
      }
      ChessState nextState = state.clone();
      nextState.play(move);
      total += perft(nextState, depth - 1);
    }

    return total;
  }

  @Test
  public void initial1() {
    assertEquals(20, perft(INITIAL, 1));
  }

  @Test
  public void initial2() {
    assertEquals(400, perft(INITIAL, 2));
  }

  @Test
  public void initial3() {
    assertEquals(8902, perft(INITIAL, 3));
  }

  @Test
  public void initial4() {
    assertEquals(197281, perft(INITIAL, 4));
  }

  @Test
  public void initial5() {
   assertEquals(4865609, perft(INITIAL, 5));
  }

  @Test
  public void kiwi1() {
    assertEquals(48, perft(KIWIPETE, 1));
  }

  @Test
  public void kiwi2() {
    assertEquals(2039, perft(KIWIPETE, 2));
  }

   @Test
   public void kiwi3() {
     perft(KIWIPETE, 3);
     assertEquals(97862, perft(KIWIPETE, 3));
   }

   @Test
   public void kiwi4() {
     perft(KIWIPETE, 4);
     assertEquals(4085603, perft(KIWIPETE, 4));
   }

  @Test
  public void pos31() {
    assertEquals(14, perft(POSITION3, 1));
  }

  @Test
  public void pos32() {
    assertEquals(191, perft(POSITION3, 2));
  }

  @Test
  public void pos33() {
    assertEquals(2812, perft(POSITION3, 3));
  }

  @Test
  public void pos34() {
    assertEquals(43238, perft(POSITION3, 4));
  }

  @Test
  public void pos35() {
   assertEquals(674624, perft(POSITION3, 5));
  }

  @Test
  public void pos36() {
   assertEquals(11030083, perft(POSITION3, 6));
  }

  @Test
  public void pos41() {
    assertEquals(6, perft(POSITION4, 1));
  }

  @Test
  public void pos42() {
    assertEquals(264, perft(POSITION4, 2));
  }

  @Test
  public void pos43() {
    assertEquals(9467, perft(POSITION4, 3));
  }

  @Test
  public void pos44() {
   assertEquals(422333, perft(POSITION4, 4));
  }

  @Test
  public void pos45() {
   assertEquals(15833292, perft(POSITION4, 5));
  }

  @Test
  public void pos4m1() {
    assertEquals(6, perft(POSITION4_MIRROR, 1));
  }

  @Test
  public void pos4m2() {
    assertEquals(264, perft(POSITION4_MIRROR, 2));
  }

  @Test
  public void pos4m3() {
    assertEquals(9467, perft(POSITION4_MIRROR, 3));
  }

  @Test
  public void pos4m4() {
   assertEquals(422333, perft(POSITION4_MIRROR, 4));
  }

  @Test
  public void pos51() {
    assertEquals(42, perft(POSITION5, 1));
  }

  @Test
  public void pos52() {
    assertEquals(1352, perft(POSITION5, 2));
  }

  @Test
  public void pos53() {
    assertEquals(53392, perft(POSITION5, 3));
  }

  @Test
  public void pos61() {
    assertEquals(46, perft(POSITION6, 1));
  }

  @Test
  public void pos62() {
    assertEquals(2079, perft(POSITION6, 2));
  }

  @Test
  public void pos63() {
   assertEquals(89890, perft(POSITION6, 3));
  }

  @Test
  public void pos64() {
   assertEquals(3894594, perft(POSITION6, 4));
  }

}
