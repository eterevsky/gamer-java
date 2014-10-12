package gamer.chess;

import static gamer.chess.Util.a2i;
import static gamer.chess.ChessState.ROOK;
import static gamer.chess.ChessState.KNIGHT;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class TestChessMove {

  @Test
  public void of() {
    for (int from = 0; from < 16; from++) {
      for (int to = 0; to < 16; to++) {
        if (from != to) {
          ChessMove move = ChessMove.of(from, to);
          assertEquals(from, move.from);
          assertEquals(to, move.to);
          assertEquals(0, move.promote);
        }
      }
    }

    ChessMove move = ChessMove.of(a2i("c7"), a2i("c8"), KNIGHT);
    assertEquals(a2i("c7"), move.from);
    assertEquals(a2i("c8"), move.to);
    assertEquals(KNIGHT, move.promote);

    move = ChessMove.of(a2i("f2"), a2i("f1"), ROOK);
    assertEquals(a2i("f2"), move.from);
    assertEquals(a2i("f1"), move.to);
    assertEquals(ROOK, move.promote);
  }
}
