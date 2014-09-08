package gamer.treegame;

import gamer.def.Move;

public final class TreeGameMove implements Move<TreeGame> {
  final Node node;

  TreeGameMove(Node node) {
    this.node = node;
  }

  public int getNodeId() {
    return node.id;
  }

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
}
