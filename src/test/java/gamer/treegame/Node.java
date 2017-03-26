package gamer.treegame;

import gamer.util.GameStatusInt;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
    Set<Node> visited = new HashSet<>();
    Queue<Node> queue = new ArrayDeque<>();
    queue.add(this);

    while (true) {
      Node n = queue.poll();
      if (n == null || n.id == id) return n;
      if (visited.contains(n)) continue;
      visited.add(n);
      for (Node child : n.children) {
        queue.add(child);
      }
    }
  }
}
