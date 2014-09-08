package gamer.treegame;

import gamer.def.GameException;
import gamer.def.GameState;
import gamer.def.GameStatus;
import gamer.def.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public final class TreeGameState implements GameState<TreeGame> {
  private final Node node;

  TreeGameState(Node node) {
    this.node = node;
  }

  public int getId() {
    return node.id;
  }

  public boolean isTerminal() {
    return node.status.isTerminal();
  }

  public GameStatus status() {
    return node.status;
  }

  public List<Move<TreeGame>> getMoves() {
    ArrayList<Move<TreeGame>> moves = new ArrayList<>();
    for (Node child : node.children) {
      moves.add(new TreeGameMove(child));
    }
    return moves;
  }

  public Move<TreeGame> getRandomMove(Random random) {
    if (node.status.isTerminal()) {
      throw new GameException("terminal state: " + this.toString());
    }
    int i = random.nextInt(node.children.size());
    return new TreeGameMove(node.children.get(i));
  }

  public TreeGameState play(Move<TreeGame> move) {
    Node newNode = ((TreeGameMove) move).node;
    if (!node.children.contains(newNode)) {
      throw new GameException();
    }
    return new TreeGameState(newNode);
  }

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

}
