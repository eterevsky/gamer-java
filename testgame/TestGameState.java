package testgame;

import java.util.Arrays;
import java.util.List;

import gamer.GameState;
import gamer.Move;

class TestGameState implements GameState<TestGame> {
  public boolean isTerminal() {
    return true;
  }

  public boolean isFirstPlayersTurn() {
    return true;
  }

  public void play(Move<TestGame> move) {
  }

  public List<Move<TestGame>> getAvailableMoves() {
    Move<TestGame> move1 = new TestGameMove(1);
    Move<TestGame> move2 = new TestGameMove(2);
    return Arrays.asList(move1, move2);
  }
}
