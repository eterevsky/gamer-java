package gamer.treegame;

import gamer.def.GameException;
import gamer.def.GameStatus;

import java.util.ArrayList;
import java.util.List;

final class Node {
  final int id;
  final GameStatus status;
  List<Node> children = new ArrayList<>();

  Node(int id, GameStatus status) {
    this.id = id;
    this.status = status;
  }

  void addChild(Node child) {
    children.add(child);
  }

  boolean getPlayer() {
    return status.getPlayer();
  }

  boolean isTerminal() {
    return status.isTerminal();
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
