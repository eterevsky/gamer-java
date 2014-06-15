package players;

class QueueElement<T> implements Comparable<QueueElement<T>> {
  final T item;
  private final Double priority;

  QueueElement(T item, double priority) {
    this.item = item;
    this.priority = priority;
  }

  public int compareTo(QueueElement o) {
    return priority.compareTo(o.priority);
  }
}
