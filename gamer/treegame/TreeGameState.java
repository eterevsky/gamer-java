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
  private Node node;

  TreeGameState(Node node) {
    this.node = node;
  }

  public boolean isTerminal() {
    return node.status.isTerminal();
  }

  public boolean getPlayer() {
    return node.status.getPlayer();
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
    int i = random.nextInt(node.children.size());
    return new TreeGameMove(node.children.get(i));
  }

  public void play(Move<TreeGame> move) throws GameException {
    Node newNode = ((TreeGameMove) move).node;
    if (!node.children.contains(newNode)) {
      throw new GameException();
    }
    node = newNode;
  }

  public GameState<TreeGame> clone() {
    return new TreeGameState(node);
  }

  public String toString() {
    return "TreeState(" + node.id + ")";
  }
}
