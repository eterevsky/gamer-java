package gamer.treegame;

import gamer.def.GameException;
import gamer.def.State;
import gamer.util.GameStatusInt;

import java.util.ArrayList;
import java.util.List;


public final class TreeGameState
    implements State<TreeGameState, TreeGameMove> {
  private Node node;
	private TreeGame game;

  TreeGameState(Node node, TreeGame game) {
    this.node = node;
		this.game = game;
  }

  @Override
  public boolean getPlayerBool() {
    return GameStatusInt.getPlayerBool(node.status);
  }

  @Override
  public boolean isTerminal() {
    return GameStatusInt.isTerminal(node.status);
  }

  @Override
  public int getPayoff(int player) {
    return GameStatusInt.getPayoff(node.status, player);
  }

  @Override
  public List<TreeGameMove> getMoves() {
    ArrayList<TreeGameMove> moves = new ArrayList<>();
    for (Node child : node.children) {
      moves.add(new TreeGameMove(child));
    }
    return moves;
  }

  public TreeGameMove getMoveToNode(int nodeId) {
    for (Node child : node.children) {
      if (child.id == nodeId) {
        return new TreeGameMove(child);
      }
    }

    return null;
  }

	@Override
	public void play(TreeGameMove move) {
    Node newNode = move.node;
    if (!node.children.contains(newNode)) {
      throw new GameException();
    }
		node = newNode;
	}

  @Override
  public TreeGameMove parseMove(String moveStr) {
    throw new UnsupportedOperationException();
  }

  @Override
  public TreeGameState clone() {
    return new TreeGameState(node, game);
  }

  @Override
  public String toString() {
    return "TreeState(" + node.id + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof TreeGameState))
      return false;
    TreeGameState oState = (TreeGameState) o;
    return node.equals(oState.node);
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(game) ^ (239 * node.id);
  }
}
