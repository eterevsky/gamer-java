package gomoku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class TestGomokuMove {

  @Test
  public void equals() {
    assertEquals(GomokuMove.create('X', 4, 5), GomokuMove.create('X', 4, 5));
    assertNotEquals(GomokuMove.create('X', 4, 5), GomokuMove.create('O', 4, 5));
    assertNotEquals(GomokuMove.create('X', 4, 5), GomokuMove.create('X', 4, 4));
    assertNotEquals(GomokuMove.create('X', 5, 5), GomokuMove.create('X', 4, 5));
  }
}
