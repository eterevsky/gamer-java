package gamer.util;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestBoard {
  @Test
  board() {
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
  }
}
