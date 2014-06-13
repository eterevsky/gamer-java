package testgame;

import gamer.Game;

public class TestGame implements Game<TestGame> {
  public static TestGameState newGame() {
    return new TestGameState();
  }
}
