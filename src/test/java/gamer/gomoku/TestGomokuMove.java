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
}
