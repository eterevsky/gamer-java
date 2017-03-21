package gamer.players;

import gamer.def.Move;
import gamer.def.State;

final class LeafNode<P extends State<P, M>, M extends Move>
    extends Node<P, M> {

  LeafNode(Node<P, M> parent, P position, M move, NodeContext<P, M> context) {
    super(parent, position, move, context);
  }

  @Override
  public Node<P, M> selectChild(P state) {
    throw new RuntimeException();
  }

  @Override
  public boolean maybeInitChildren(P state) {
    return false;
  }
}
