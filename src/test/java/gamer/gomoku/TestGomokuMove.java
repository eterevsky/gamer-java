package gamer.gomoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class TestGomokuMove {

  @Test
  public void equals() {
    assertEquals(GomokuMove.of(4, 5), GomokuMove.of(4, 5));
    assertNotEquals(GomokuMove.of(4, 5), GomokuMove.of(4, 4));
    assertNotEquals(GomokuMove.of(5, 5), GomokuMove.of(4, 5));
  }

  @Test
  public void parse() {
    assertEquals(GomokuMove.of(0, 0), GomokuMove.of("a1"));
    assertEquals(GomokuMove.of(5, 4), GomokuMove.of("f5"));
    assertEquals(GomokuMove.of(5, 4), GomokuMove.of("F5"));
    assertEquals(GomokuMove.of(10, 10), GomokuMove.of("L11"));
  }

  @Test(expected=RuntimeException.class)
  public void parseError1() {
    GomokuMove.of("xx");
  }

  @Test(expected=RuntimeException.class)
  public void parseError2() {
    GomokuMove.of("a239");
  }
}
