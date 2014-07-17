package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;

import java.util.Collection;

abstract class BanditSelector<G extends Game> implements Node.Selector<G> {
  protected Node<G> node = null;

  public void setNode(Node<G> node) {
    this.node = node;
  }

  public Node<G> select(Collection<Node<G>> children,
                        long samples,
                        long pendingSamples) {
    double totalSamplesLog = 2 * Math.log(samples + pendingSamples);
    assert totalSamplesLog >= 0;

    Node<G> bestChild = null;
    double bestChildPrio = 0;
    for (Node<G> child : children) {
      if (child.getSamplesWithPending() == 0) {
        return child;
      }
      double priority = child.getUcbPriority(
          totalSamplesLog, node.getState().status().getPlayer());

      if (bestChild == null || priority > bestChildPrio) {
        bestChild = child;
        bestChildPrio = priority;
      }
    }

    return bestChild;
  }

  public abstract boolean shouldCreateChildren();

  public abstract Node.Selector<G> newChildSelector();

  public void childUpdated(Node<G> child) {}
}
