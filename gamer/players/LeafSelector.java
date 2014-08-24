package gamer.players;

import gamer.def.Game;

import java.util.Collection;

final class LeafSelector<G extends Game> implements Node.Selector<G> {
  public void setNode(Node<G> node) {}

  public Node<G> select(Collection<Node<G>> children, long totalSamples)  {
    throw new RuntimeException();
  }

  public boolean shouldCreateChildren() {
    return false;
  }

  public Node.Selector<G> newChildSelector() {
    throw new RuntimeException();
  }

  public void childUpdated(Node<G> child, long totalSamples) {
    throw new RuntimeException();
  }
}
