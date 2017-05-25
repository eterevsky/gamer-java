package gamer.go;

import gamer.def.GameException;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestGoState {
  // @Test(timeout=500)
  public void playRandomGame() {
    GoState state = Go.getInstance().newGame();
    while (!state.isTerminal()) {
      state.play(state.getRandomMove());
    }
  }
}
