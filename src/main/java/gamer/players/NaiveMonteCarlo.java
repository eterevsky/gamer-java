package gamer.players;

import gamer.def.Move;
import gamer.def.Position;

import java.util.Collection;
import java.util.Iterator;

public class NaiveMonteCarlo<P extends Position<P, M>, M extends Move>
    extends GenericPlayer<P, M> {
  static class Selector<P extends Position<P, M>, M extends Move>
      implements Node.Selector<P, M> {
    Iterator<Node<P, M>> childrenIt = null;

    @Override
    public void setNode(Node<P, M> node) {}

    @Override
    public synchronized Node<P, M> select(
        Collection<Node<P, M>> children, long totalSamples) {
      if (childrenIt == null || !childrenIt.hasNext())
        childrenIt = children.iterator();

      return childrenIt.next();
    }

    @Override
    public boolean shouldCreateChildren() {
      return true;
    }

    @Override
    public synchronized LeafSelector<P, M> newChildSelector() {
      return new LeafSelector<P, M>();
    }
  }

  @Override
  protected Node<P, M> getRoot(P state) {
    return new Node<P, M>(null, state, null, new Selector<P, M>(), nodeContext);
  }
}
