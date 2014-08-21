package gamer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UpdatablePriorityQueue<T> {
  private static class Element<T> {
    Element(T value, double priority) {
      this.value = value;
      this.priority = priority;
    }

    T value;
    double priority;
  }

  private List<Element<T>> heap = new ArrayList<>();
  private Map<T, Integer> position = new HashMap<>();

  public UpdatablePriorityQueue() {}

  public void add(T value, double priority) {
    heap.add(new Element<>(value, priority));
    bubbleUp(heap.size() - 1);
  }

  public void update(T value, double priority) {
    int index = position.get(value);
    Element<T> element = heap.get(index);
    double oldPriority = element.priority;
    element.priority = priority;
    if (priority >= oldPriority) {
      bubbleUp(index);
    } else {
      bubbleDown(index);
    }
  }

  public T head() {
    return heap.get(0).value;
  }

  private void bubbleUp(int index) {
    Element<T> element = heap.get(index);
    while (index > 0 && heap.get((index - 1) / 2).priority < element.priority) {
      heap.set(index, heap.get((index - 1) / 2));
      position.put(heap.get(index).value, index);
      index = (index - 1) / 2;
    }

    heap.set(index, element);
    position.put(element.value, index);
  }

  private void bubbleDown(int index) {
    Element<T> element = heap.get(index);
    while (true) {
      int leftChild = 2*index + 1;
      int rightChild = leftChild + 1;
      if (leftChild >= heap.size())
        break;

      int maxChild;

      if (heap.get(leftChild).priority > element.priority) {
        if (rightChild < heap.size() &&
            heap.get(rightChild).priority > heap.get(leftChild).priority) {
          maxChild = rightChild;
        } else {
          maxChild = leftChild;
        }
      } else {
        if (rightChild < heap.size() &&
            heap.get(rightChild).priority > element.priority) {
          maxChild = rightChild;
        } else {
          break;
        }
      }

      heap.set(index, heap.get(maxChild));
      position.put(heap.get(index).value, index);
      index = maxChild;
    }

    heap.set(index, element);
    position.put(element.value, index);
  }
}
