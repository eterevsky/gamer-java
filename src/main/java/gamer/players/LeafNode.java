package gamer.players;

import gamer.def.Move;
import gamer.def.Position;

final class LeafNode<P extends Position<P, M>, M extends Move>
    extends Node<P, M> {

  LeafNode(Node<P, M> parent, P position, M move, NodeContext<P, M> context) {
    super(parent, position, move, context);
  }

  @Override
  public Node<P, M> selectChild() {
    throw new RuntimeException();
  }

  @Override
  public boolean maybeInitChildren() {
    return false;
  }
}
