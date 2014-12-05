package gamer.treegame;

import gamer.def.GameException;
import gamer.def.Position;
import gamer.def.TerminalPositionException;
import gamer.util.GameStatusInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public final class TreeGameState
    implements Position<TreeGameState, TreeGameMove> {
  private final Node node;

  TreeGameState(Node node) {
    this.node = node;
  }

  public int getId() {
    return node.id;
  }

  @Override
  public boolean isTerminal() {
    return GameStatusInt.isTerminal(node.status);
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
  public TreeGameMove getRandomMove(Random random) {
    if (GameStatusInt.isTerminal(node.status)) {
      throw new TerminalPositionException("terminal state: " + this.toString());
    }
    int i = random.nextInt(node.children.size());
    return new TreeGameMove(node.children.get(i));
  }

  @Override
  public TreeGameState play(TreeGameMove move) {
    Node newNode = move.node;
    if (!node.children.contains(newNode)) {
      throw new GameException();
    }
    return new TreeGameState(newNode);
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
  public String moveToString(TreeGameMove move) {
    return move.toString();
  }

  @Override
  public int getPlayer() {
    return getPlayerBool() ? 0 : 1;
  }

  @Override
  public boolean getPlayerBool() {
    return GameStatusInt.getPlayerBool(node.status);
  }

  @Override
  public int getPayoff(int player) {
    return GameStatusInt.getPayoff(node.status, player);
  }

  @Override
  public TreeGameMove parseMove(String moveStr) {
    throw new UnsupportedOperationException();
  }
}
