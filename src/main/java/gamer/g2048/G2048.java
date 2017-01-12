package gamer.g2048;

import gamer.def.Game;
import gamer.def.MoveSelector;
import gamer.def.Position;

public final class G2048 implements Game<G2048State, G2048Move> {
  public G2048State newGame();
  private static G2048 INSTANCE = new G2048();
  private static G2048State.RandomSelector RANDOM_SELECTOR =
      new G2048State.RandomSelector();

  private G2048() {
  }

  public static G2048 getInstance() {
    return INSTANCE;
  }

  public MoveSelector<P, M> getRandomMoveSelector() {
    return RANDOM_SELECTOR;
  }

  public MoveSelector<P, M> getMoveSelector(String selectorType) {
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
