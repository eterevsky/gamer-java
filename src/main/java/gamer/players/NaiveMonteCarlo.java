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

    public void setNode(Node<P, M> node) {}

    public synchronized Node<P, M> select(
        Collection<Node<P, M>> children, long totalSamples) {
      if (childrenIt == null || !childrenIt.hasNext())
        childrenIt = children.iterator();

      return childrenIt.next();
    }

    public boolean shouldCreateChildren() {
      return true;
    }

    public synchronized LeafSelector<P, M> newChildSelector() {
      return new LeafSelector<P, M>();
    }

    public void childUpdated(Node<P, M> child, long totalSamples) {}
  }

  @Override
  protected Node<P, M> getRoot(P state) {
    return new Node<P, M>(null, state, null, new Selector<P, M>(), nodeContext);
  }
}
