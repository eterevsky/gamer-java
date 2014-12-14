package gamer.players;

import gamer.def.Move;
import gamer.def.Position;

abstract class BanditNode<P extends Position<P, M>, M extends Move>
    extends Node<P, M> {

  BanditNode(Node<P, M> parent, P position, M move, NodeContext<P, M> context) {
    super(parent, position, move, context);
  }

  @Override
  public Node<P, M> selectChild() {
    double totalSamplesLog = 2 * Math.log(getTotalSamples());
    assert totalSamplesLog >= 0;

    Node<P, M> bestChild = null;
    double bestChildPrio = 0;
    boolean player = getPosition().getPlayerBool();

    for (Node<P, M> child : children) {
      if (child.getTotalSamples() == 0) {
        return child;
      }
      double priority = child.getUcbPriority(totalSamplesLog, player);

      if (bestChild == null || priority > bestChildPrio) {
        bestChild = child;
        bestChildPrio = priority;
      }
    }

    return bestChild;
  }
}
