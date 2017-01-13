package gamer.treegame;

import gamer.def.Move;

public final class TreeGameMove implements Move {
  final Node node;

  TreeGameMove(Node node) {
    this.node = node;
  }

  public int getNodeId() {
    return node.id;
  }

  @Override
  public String toString() {
    return "-> " + node.id;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof TreeGameMove))
      return false;
    TreeGameMove oMove = (TreeGameMove) o;
    return node.equals(oMove.node);
  }

  @Override
  public int hashCode() {
    return node.id;
  }
}
