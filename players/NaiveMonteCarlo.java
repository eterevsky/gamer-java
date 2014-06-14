package players;

import gamer.Game;
import gamer.GameState;
import gamer.Move;
import gamer.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NaiveMonteCarlo implements Player {
  private final long timeout;
  private final int MAX_SAMPLERS = 32;

  public NaiveMonteCarlo(long timeout) {
    this.timeout = timeout;
  }

  public <T extends Game> Move<T> selectMove(GameState<T> state)
      throws Exception {
    long startTime = System.currentTimeMillis();

    List<Move<T>> moves = state.getAvailableMoves();
    int[] resultByMove = new int[moves.size()];
    int[] totalByMove = new int[moves.size()];
    Arrays.fill(resultByMove, 0);
    Arrays.fill(totalByMove, 0);
    boolean iAmFirst = state.isFirstPlayersTurn();

    ExecutorService executor = Executors.newFixedThreadPool(32);
    CompletionService<Sample<Integer>> compService =
        new ExecutorCompletionService<>(executor);

    int imove = 0;
    int runningSamplers = 0;

    while (System.currentTimeMillis() - startTime < timeout) {
      while (runningSamplers < MAX_SAMPLERS) {
        GameState<T> mcState = state.clone();
        mcState.play(moves.get(imove));
        compService.submit(new RandomSampler<Integer>(imove, mcState, 10));
        runningSamplers += 1;
        imove = (imove + 1) % moves.size();
      }

      Sample<Integer> sample = compService.take().get();
      runningSamplers -= 1;

      totalByMove[sample.label] += sample.nsamples;
      if (iAmFirst) {
        resultByMove[sample.label] += sample.result;
      } else {
        resultByMove[sample.label] -= sample.result;
      }
    }

    executor.shutdownNow();

    int bestMove = 0;
    for (int i = 1; i < moves.size(); i++) {
      if (resultByMove[i] > resultByMove[bestMove])
        bestMove = i;
    }

    System.out.format("%d of %d\n",
                      (totalByMove[bestMove] + resultByMove[bestMove]) / 2,
                      totalByMove[bestMove]);

    return moves.get(bestMove);
  }
}
