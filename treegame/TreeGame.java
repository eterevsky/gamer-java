package treegame;

import gamer.Game;
import gamer.GameResult;

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

    public Builder addTermNode(int id, boolean player, GameResult result) {
      nodes.put(id, new Node(id, player, true, result));
      return this;
    }

    public Builder addNode(int id, boolean player) {
      nodes.put(id, new Node(id, player, false, null));
      return this;
    }

    public Builder addMove(int from, int to) {
      nodes.get(from).addChild(nodes.get(to));
      return this;
    }

    public Builder setRoot(int id) {
      rootId = id;
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
