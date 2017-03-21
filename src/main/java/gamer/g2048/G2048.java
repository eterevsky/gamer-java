package gamer.g2048;

import gamer.def.Game;
import gamer.def.MoveSelector;
import gamer.util.Board;

import java.util.Arrays;

public final class G2048 implements Game<G2048State, G2048Move> {
  private static G2048 INSTANCE = new G2048();
  private static G2048State.RandomSelector RANDOM_SELECTOR =
      new G2048State.RandomSelector();
  static Board BOARD = new Board(4, 4, Arrays.asList(
      ".", "2", "4", "8", "16", "32", "64", "128", "256",
      "512", "1024", "2048", "4096", "8192", "16384", "32768",
      "65536"));

  private G2048() {
  }

  public G2048State newGame() {
    return new G2048State();
  }

  public static G2048 getInstance() {
    return INSTANCE;
  }

  public MoveSelector<G2048State, G2048Move> getRandomMoveSelector() {
    return RANDOM_SELECTOR;
  }

  public MoveSelector<G2048State, G2048Move> getMoveSelector(
      String selectorType) {
    if (selectorType == "random") {
      return RANDOM_SELECTOR;
    } else {
      throw new IllegalArgumentException();
    }
  }

  public int getPlayersCount() {
    return 1;
  }

  public boolean isRandom() {
    return true;
  }

  public int getMaxPayoff() {
    return 65536;
  }

  public int getMinPayoff() {
    return 0;
  }
}
