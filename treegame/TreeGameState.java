package treegame;

import gamer.GameException;
import gamer.GameState;
import gamer.GameResult;
import gamer.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public final class TreeGameState implements GameState<TreeGame> {
  private Node node;

  TreeGameState(Node node) {
    this.node = node;
  }

  public boolean isTerminal() {
    return node.isTerminal();
  }

  public boolean getPlayer() {
    return node.getPlayer();
  }

  public GameResult getResult() throws GameException {
    return node.getResult();
  }

  public List<Move<TreeGame>> getAvailableMoves() {
    ArrayList<Move<TreeGame>> moves = new ArrayList<>();
    for (Node child : node.children) {
      moves.add(new TreeGameMove(child));
    }
    return moves;
  }

  public Move<TreeGame> getRandomMove() {
    int i = ThreadLocalRandom.current().nextInt(node.children.size());
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
}
