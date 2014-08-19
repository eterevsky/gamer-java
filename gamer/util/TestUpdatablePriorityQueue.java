package gamer.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class TestUpdatablePriorityQueue {

  @Test(timeout=10)
  public void run() {
    UpdatablePriorityQueue<Integer> queue = new UpdatablePriorityQueue<>();
    queue.add(1, 0.5);
    assertEquals(Integer.valueOf(1), queue.head());

    queue.add(2, 1.0);
    assertEquals(Integer.valueOf(2), queue.head());

    queue.update(1, 2.0);
    assertEquals(Integer.valueOf(1), queue.head());

    queue.add(3, 0.5);
    assertEquals(Integer.valueOf(1), queue.head());

    queue.add(4, 0.5);
    assertEquals(Integer.valueOf(1), queue.head());

    queue.update(3, 0.6);
    queue.update(1, 0.1);
    assertEquals(Integer.valueOf(2), queue.head());

    queue.update(2, 0.0);
    assertEquals(Integer.valueOf(3), queue.head());
  }
}
