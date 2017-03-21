package gamer.players;

import gamer.def.Move;
import gamer.def.State;

abstract class BanditNode<S extends State<S, M>, M extends Move>
    extends Node<S, M> {

  BanditNode(Node<S, M> parent, S position, M move, NodeContext<S, M> context) {
    super(parent, position, move, context);
  }

  @Override
  public Node<S, M> selectChild(S state) {
    double totalSamplesLog = Math.log(getTotalSamples());
    assert totalSamplesLog >= 0;

    Node<S, M> bestChild = null;
    double bestChildPrio = 0;

    if (getPlayer() < 0) {
      assert !knowExact();
      return selectRandomChild(state);
    }

    for (Node<S, M> child : children) {
      if (child.getTotalSamples() == 0) {
        return child;
      }
      double priority = child.getUcbPriority(totalSamplesLog,
                                             getPlayer() == 0);

      if (bestChild == null || priority > bestChildPrio) {
        bestChild = child;
        bestChildPrio = priority;
      }
    }

    return bestChild;
  }
}
