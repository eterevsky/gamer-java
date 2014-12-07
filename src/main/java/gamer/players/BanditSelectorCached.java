package gamer.players;

import gamer.def.Move;
import gamer.def.Position;
import gamer.util.UpdatablePriorityQueue;

import java.util.Collection;

abstract class BanditSelectorCached<P extends Position<P, M>, M extends Move>
    implements Node.Selector<P, M> {
  protected Node<P, M> node = null;
  private UpdatablePriorityQueue<Node<P, M>> queue = null;
  long nextFullUpdate;

  public void setNode(Node<P, M> node) {
    this.node = node;
  }

  public Node<P, M> select(Collection<Node<P, M>> children, long totalSamples) {
    throw new RuntimeException(
        "Shouldn't be used, since childUpdate is switched off.");

//	if (queue == null || totalSamples > nextFullUpdate)
//	  initQueue(children, totalSamples);
//	
//	 return queue.head();
  }

//  private void initQueue(Collection<Node<P, M>> children, long totalSamples) {
//    queue = new UpdatablePriorityQueue<>(children.size());
//    double totalSamplesLog = 2 * Math.log(totalSamples);
//    boolean player = node.getPosition().getPlayerBool();
//
//    for (Node<P, M> child : children) {
//      queue.add(child, child.getUcbPriority(totalSamplesLog, player));
//    }
//
//    nextFullUpdate = (long)(totalSamples * 1.1);
//  }

  public abstract boolean shouldCreateChildren();

  public abstract Node.Selector<P, M> newChildSelector();

  public void childUpdated(Node<P, M> child, long totalSamples) {
    boolean player = node.getPosition().getPlayerBool();
    queue.update(
        child, child.getUcbPriority(2 * Math.log(totalSamples), player));
  }
}
