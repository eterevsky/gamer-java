package gamer.players;

import gamer.def.Move;
import gamer.def.Position;

import java.util.Collection;

abstract class BanditSelector<P extends Position<P, M>, M extends Move>
    implements Node.Selector<P, M> {
  protected Node<P, M> node = null;

  public void setNode(Node<P, M> node) {
    this.node = node;
  }

  public Node<P, M> select(Collection<Node<P, M>> children, long totalSamples) {
    double totalSamplesLog = 2 * Math.log(totalSamples);
    assert totalSamplesLog >= 0;

    Node<P, M> bestChild = null;
    double bestChildPrio = 0;
    for (Node<P, M> child : children) {
      if (child.getTotalSamples() == 0) {
        return child;
      }
      double priority = child.getUcbPriority(
          totalSamplesLog, node.getPosition().getPlayerBool());

      if (bestChild == null || priority > bestChildPrio) {
        bestChild = child;
        bestChildPrio = priority;
      }
    }

    return bestChild;
  }

  public abstract boolean shouldCreateChildren();

  public abstract Node.Selector<P, M> newChildSelector();

  public void childUpdated(Node<P, M> child, long totalSamples) {}
}
