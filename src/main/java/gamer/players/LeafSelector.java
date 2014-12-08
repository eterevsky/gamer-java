package gamer.players;

import gamer.def.Move;
import gamer.def.Position;

import java.util.Collection;

final class LeafSelector<P extends Position<P, M>, M extends Move>
    implements Node.Selector<P, M> {
  @Override
  public void setNode(Node<P, M> node) {}

  @Override
  public Node<P, M> select(Collection<Node<P, M>> children, long totalSamples) {
    throw new RuntimeException();
  }

  @Override
  public boolean shouldCreateChildren() {
    return false;
  }

  @Override
  public Node.Selector<P, M> newChildSelector() {
    throw new RuntimeException();
  }
}
