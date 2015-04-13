package gamer.gomoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class TestGomokuMove {

  @Test
  public void equals() {
    assertEquals(GomokuMove.of(4, 5, 19), GomokuMove.of(4, 5, 19));
    assertNotEquals(GomokuMove.of(4, 5, 19), GomokuMove.of(4, 4, 19));
    assertNotEquals(GomokuMove.of(5, 5, 19), GomokuMove.of(4, 5, 19));
  }

  @Test
  public void parse() {
    assertEquals(GomokuMove.of(0, 0, 19), GomokuMove.of("a1", 19));
    assertEquals(GomokuMove.of(5, 4, 19), GomokuMove.of("f5", 19));
    assertEquals(GomokuMove.of(5, 4, 19), GomokuMove.of("F5", 19));
    assertEquals(GomokuMove.of(10, 10, 19), GomokuMove.of("L11", 19));
  }

  @Test(expected=RuntimeException.class)
  public void parseError1() {
    GomokuMove.of("xx", 19);
  }

  @Test(expected=RuntimeException.class)
  public void parseError2() {
    GomokuMove.of("a239", 19);
  }
}
