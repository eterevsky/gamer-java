package treegame;

import gamer.GameException;
import gamer.GameResult;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

final class Node {
  private enum Properties {
    PLAYER,
    TERMINAL,
    WIN,
    DRAW
  }

  final int id;
  private final EnumSet<Properties> properties =
      EnumSet.noneOf(Properties.class);
  List<Node> children = new ArrayList<>();

  Node(int id, boolean player, boolean terminal, GameResult result) {
    this.id = id;
    if (player) {
      properties.add(Properties.PLAYER);
    }
    if (terminal) {
      properties.add(Properties.TERMINAL);
      switch (result) {
        case WIN:  properties.add(Properties.WIN); break;
        case DRAW: properties.add(Properties.DRAW); break;
      }
    }
  }

  void addChild(Node child) {
    children.add(child);
  }

  boolean getPlayer() {
    return properties.contains(Properties.PLAYER);
  }

  boolean isTerminal() {
    return properties.contains(Properties.TERMINAL);
  }

  GameResult getResult() throws GameException {
    if (!isTerminal()) {
      throw new GameException();
    }
    if (properties.contains(Properties.WIN)) {
      return GameResult.WIN;
    }
    if (properties.contains(Properties.DRAW)) {
      return GameResult.DRAW;
    } else {
      return GameResult.LOSS;
    }
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
