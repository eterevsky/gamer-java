package treegame;

import gamer.Move;

public final class TreeGameMove implements Move<TreeGame> {
  final Node node;

  TreeGameMove(Node node) {
    this.node = node;
  }
}
