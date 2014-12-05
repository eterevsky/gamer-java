package gamer.treegame;

import gamer.util.GameStatusInt;

import java.util.ArrayList;
import java.util.List;

final class Node {
  final int id;
  final int status;
  List<Node> children = new ArrayList<>();

  Node(int id, int status) {
    this.id = id;
    this.status = status;
  }

  void addChild(Node child) {
    children.add(child);
  }

  boolean getPlayerBool() {
    return GameStatusInt.getPlayerBool(status);
  }

  boolean isTerminal() {
    return GameStatusInt.isTerminal(status);
  }

  Node getDescendantById(int id) {
    if (this.id == id)
      return this;
    for (Node child : children) {
      Node found = child.getDescendantById(id);
      if (found != null)
        return found;
    }
    return null;
  }
}
