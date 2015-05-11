package gamer.treegame;

import gamer.def.Game;
import gamer.def.GameException;
import gamer.util.GameStatusInt;

import java.util.HashMap;
import java.util.Map;

/**
 * Used mainly for unit tests which require games with very simple game trees.
 */
public final class TreeGame implements Game {
  final Node root;

  private TreeGame(Node root) {
    this.root = root;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public TreeGameState newGame() {
    return new TreeGameState(root, this);
  }

  Node getNode(int id) {
    return root.getDescendantById(id);
  }

  public static class Builder {
    Map<Integer, Node> nodes = new HashMap<>();
    int rootId = -1;

    Builder() {}

    public TreeGame toGame() {
      TreeGame game = new TreeGame(nodes.get(rootId));
      nodes = null;
      return game;
    }

    public Builder addNode(int id, int status) {
      nodes.put(id, new Node(id, status));
      return this;
    }

    public Builder addMove(int from, int to) {
      if (!nodes.containsKey(to)) {
        this.addNode(to, GameStatusInt.switchPlayer(nodes.get(from).status));
      }
      nodes.get(from).addChild(nodes.get(to));
      return this;
    }

    public Builder addLastMove(int from, int to, int status)
        throws GameException {
      if (!GameStatusInt.isTerminal(status)) {
        throw new RuntimeException();
      }

      if (nodes.containsKey(to)) {
        if (nodes.get(to).status != status) {
          throw new GameException();
        }
      } else {
        this.addNode(to, status);
      }
      return this.addMove(from, to);
    }

    public Builder setRoot(int id) {
      rootId = id;
      this.addNode(id, GameStatusInt.init());
      return this;
    }
  }
}
