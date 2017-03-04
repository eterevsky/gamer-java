package gamer.util;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestBoard {
  @Test
  public void board() {
    Board board = new Board(3, 2, Arrays.asList(".", "x", "o"));
    assertEquals(-3, board.down());
    assertEquals(3, board.up());
    assertEquals(1, board.right());
    assertEquals(-1, board.left());

    assertEquals(4, board.tile(1, 1));
    assertEquals(2, board.x(5));
    assertEquals(1, board.y(5));

    assertEquals("A2", board.tileToString(3));
    assertEquals("B1", board.tileToString(1));
    assertEquals(3, board.parseTile("a2"));
    assertEquals(1, board.parseTile("B1"));

    byte[] b = new byte[6];
    b[0] = 1;
    b[4] = 2;
    b[3] = 1;

    assertEquals(String.format(" x o .%n x . .%n"),
                 board.boardToString(b, false));

    assertEquals(String.format(" 2 x o .%n 1 x . .%n   A B C%n"),
                 board.boardToString(b, true));
  }
}
