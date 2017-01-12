package gamer.g2048;

import gamer.def.Game;
import gamer.def.MoveSelector;
import gamer.def.Position;

public final class G2048 implements Game<G2048State, G2048Move> {
  private static G2048 INSTANCE = new G2048();
  private static G2048State.RandomSelector RANDOM_SELECTOR =
      new G2048State.RandomSelector();

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

  public MoveSelector<G2048State, G2048Move> getMoveSelector(String selectorType) {
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
}
