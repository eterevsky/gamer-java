package players;

/* package */ class QueueElement<T> implements Comparable<QueueElement<T>> {
  /* package */ final T item;
  private final Double priority;

  QueueElement(T item, double priority) {
    this.item = item;
    this.node = node;
  }

  public int compareTo(QueuedItem o) {
    return priority.compareTo(o.priority);
  }
}
