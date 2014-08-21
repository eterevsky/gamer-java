package gamer.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

  @Test(timeout=50)
  public void randomized() {
    Random random = new Random(123456789L);

    UpdatablePriorityQueue<Integer> queue = new UpdatablePriorityQueue<>();
    List<Double> prio = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      prio.add(random.nextDouble());
      queue.add(i, prio.get(i));

      int head = queue.head();

      for (int j = 0; j < i; j++) {
        assertTrue(prio.get(j) <= prio.get(head));
      }
    }

    Integer head = queue.head();

    for (int it = 0; it < 1000; it++) {
      int i = random.nextInt(100);
      prio.set(i, random.nextDouble());
      queue.update(i, prio.get(i));
      if (i != head && prio.get(head) >= prio.get(i)) {
        assertEquals(head, queue.head());
      } else {
        head = queue.head();
        for (int j = 0; j < 100; j++) {
          assertTrue(prio.get(j) <= prio.get(head));
        }
      }
    }
  }
}
