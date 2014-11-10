package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.util.UpdatablePriorityQueue;

import java.util.Collection;

abstract class BanditSelectorCached<G extends Game>
    implements Node.Selector<G> {
  protected Node<G> node = null;
  private UpdatablePriorityQueue<Node<G>> queue = null;
  long nextFullUpdate;

  public void setNode(Node<G> node) {
    this.node = node;
  }

  public Node<G> select(Collection<Node<G>> children, long totalSamples) {
    throw new RuntimeException("Shouldn't be used, since childUpdate is switched off.");

    // if (queue == null || totalSamples > nextFullUpdate)
    //   initQueue(children, totalSamples);
    //
    // return queue.head();
  }

  private void initQueue(Collection<Node<G>> children, long totalSamples) {
    queue = new UpdatablePriorityQueue<>(children.size());
    double totalSamplesLog = 2 * Math.log(totalSamples);
    boolean player = node.getState().status().getPlayer();

    for (Node<G> child : children) {
      queue.add(child, child.getUcbPriority(totalSamplesLog, player));
    }

    nextFullUpdate = (long)(totalSamples * 1.1);
  }

  public abstract boolean shouldCreateChildren();

  public abstract Node.Selector<G> newChildSelector();

  public void childUpdated(Node<G> child, long totalSamples) {
    boolean player = node.getState().status().getPlayer();
    queue.update(
        child, child.getUcbPriority(2 * Math.log(totalSamples), player));
  }
}
