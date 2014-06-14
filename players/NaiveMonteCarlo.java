package players;

import gamer.Game;
import gamer.GameState;
import gamer.Move;
import gamer.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NaiveMonteCarlo implements Player {
  private final long timeout;

  public NaiveMonteCarlo(long timeout) {
    this.timeout = timeout;
  }

  public <T extends Game> Move<T> selectMove(GameState<T> state)
      throws RuntimeException {
    long startTime = System.currentTimeMillis();

    Player randomPlayer = new RandomPlayer();
    List<Move<T>> moves = state.getAvailableMoves();
    int[] resultByMove = new int[moves.size()];
    Arrays.fill(resultByMove, 0);
    int total = 0;
    boolean iAmFirst = state.isFirstPlayersTurn();

    while (System.currentTimeMillis() - startTime < timeout) {
      total += 1;
      for (int i = 0; i < moves.size(); i++) {
        GameState<T> mcState = state.clone();
        mcState.play(moves.get(i));
        while (!mcState.isTerminal()) {
          mcState.play(randomPlayer.selectMove(mcState));
        }
        if (iAmFirst) {
          resultByMove[i] += mcState.getResult();
        } else {
          resultByMove[i] -= mcState.getResult();
        }
      }
    }

    int bestMove = 0;
    for (int i = 1; i < moves.size(); i++) {
      if (resultByMove[i] > resultByMove[bestMove])
        bestMove = i;
    }

    System.out.format("%d of %d\n",
                      (total + resultByMove[bestMove]) / 2,
                      total);

    return moves.get(bestMove);
  }
}
