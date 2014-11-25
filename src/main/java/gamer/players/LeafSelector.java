package gamer.players;

import gamer.def.Move;
import gamer.def.Position;

import java.util.Collection;

final class LeafSelector<P extends Position<P, M>, M extends Move>
    implements Node.Selector<P, M> {
  public void setNode(Node<P, M> node) {}

  public Node<P, M> select(Collection<Node<P, M>> children, long totalSamples) {
    throw new RuntimeException();
  }

  public boolean shouldCreateChildren() {
    return false;
  }

  public Node.Selector<P, M> newChildSelector() {
    throw new RuntimeException();
  }

  public void childUpdated(Node<P, M> child, long totalSamples) {
    throw new RuntimeException();
  }
}
