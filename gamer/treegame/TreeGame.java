package gamer.treegame;

import gamer.def.Game;
import gamer.def.GameException;
import gamer.def.GameResult;

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

    public Builder addTermNode(int id, GameResult result) {
      nodes.put(id, new Node(id, true, true, result));
      return this;
    }

    public Builder addNode(int id, boolean player) {
      nodes.put(id, new Node(id, player, false, null));
      return this;
    }

    public Builder addMove(int from, int to) {
      if (!nodes.containsKey(to)) {
        this.addNode(to, !nodes.get(from).getPlayer());
      }
      nodes.get(from).addChild(nodes.get(to));
      return this;
    }

    public Builder addLastMove(int from, int to, GameResult result)
        throws GameException {
      if (nodes.containsKey(to)) {
        if (nodes.get(to).getResult() != result) {
          throw new GameException();
        }
      } else {
        this.addTermNode(to, result);
      }
      return this.addMove(from, to);
    }

    public Builder setRoot(int id) {
      rootId = id;
      this.addNode(id, true);
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

  // For testing.
  Node getNode(int id) {
    return root.getDescendantById(id);
  }
}
