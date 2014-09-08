package gamer.treegame;

import gamer.def.Game;
import gamer.def.GameException;
import gamer.def.GameStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

// Used mainly for unit tests which require games with very simple game trees.
public final class TreeGame implements Game<TreeGame> {
  private final Node root;

  public static class Builder {
    Map<Integer, Node> nodes = new HashMap<>();
    int rootId = -1;

    Builder() {
    }

    public TreeGame toGame() {
      TreeGame game = new TreeGame(nodes.get(rootId));
      nodes = null;
      return game;
    }

    public Builder addNode(int id, GameStatus status) {
      nodes.put(id, new Node(id, status));
      return this;
    }

    public Builder addMove(int from, int to) {
      if (!nodes.containsKey(to)) {
        this.addNode(to, nodes.get(from).status.otherPlayer());
      }
      nodes.get(from).addChild(nodes.get(to));
      return this;
    }

    public Builder addLastMove(int from, int to, GameStatus status)
        throws GameException {
      assert status.isTerminal();
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
      this.addNode(id, GameStatus.FIRST_PLAYER);
      return this;
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  private TreeGame(Node root) {
    this.root = root;
  }

  public TreeGameState newGame() {
    return new TreeGameState(root);
  }

  Node getNode(int id) {
    return root.getDescendantById(id);
  }
}
