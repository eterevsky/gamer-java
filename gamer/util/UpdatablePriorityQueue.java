package gamer.util;

import java.util.HashMap;
import java.util.Map;

public final class UpdatablePriorityQueue<T> {
  private static class Element {
    Element(T value, double priority) {
      this.value = value;
      this.priority = priority;
    }

    T value;
    double priority;
  }

  private Element[] heap;
  private Map<T, Integer> position;
  private int currentSize;

  public UpdatablePriorityQueue(int size) {
    heap = new T[size];
    position = new HashMap<>();
  }

  public void add(T element, double priority) {
  }

  public void update(T element, double priority) {
  }

  public T head() {
    if (currentSize == 0) {
      throw new IndexOutOfBoundsException();
    }

    return heap[0].value;
  }
}
