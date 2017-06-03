package gamer;

import gamer.def.State;
import gamer.gomoku.Gomoku;
import gamer.gomoku.GomokuState;
import javafx.concurrent.Task;

public class GamerTask extends Task<GamerState> {
  @Override
  protected GamerState call() {
    GomokuState gameState = Gomoku.getInstance().newGame();
    for (int i = 0; i < 1000; i++) {
      if (gameState.isTerminal()) {
        gameState = Gomoku.getInstance().newGame();
      }
      gameState.play(gameState.getRandomMove());
      GamerState state = new GamerState(gameState,
                                        String.format("report %d", i));

      updateValue(state);
      try {
        Thread.sleep(500);
      } catch (InterruptedException interrupted) {
        if (isCancelled()) {
          System.out.println("Cancelled.");
          updateMessage("Cancelled");
          break;
        }
      }
    }

    return new GamerState(gameState, "finished report");
  }
}
