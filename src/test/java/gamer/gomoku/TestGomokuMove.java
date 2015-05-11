package gamer.gomoku;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestGomokuMove {
  @BeforeClass
  public static void setup() {
    GomokuMove.createInstances(19);
  }

  @Test
  public void equals() {
    assertEquals(GomokuMove.of(0, 0, 9), GomokuMove.of(0, 0, 9));
    assertEquals(GomokuMove.of(4, 5, 19), GomokuMove.of(4, 5, 19));
    assertNotEquals(GomokuMove.of(4, 5, 19), GomokuMove.of(4, 4, 19));
    assertNotEquals(GomokuMove.of(5, 5, 10), GomokuMove.of(4, 5, 10));
  }

  @Test
  public void parse() {
    assertEquals(GomokuMove.of(0, 0, 9), GomokuMove.of("a1", 9));
    assertEquals(GomokuMove.of(5, 4, 19), GomokuMove.of("f5", 19));
    assertEquals(GomokuMove.of(5, 4, 11), GomokuMove.of("F5", 11));
    assertEquals(GomokuMove.of(10, 10, 11), GomokuMove.of("L11", 11));
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
