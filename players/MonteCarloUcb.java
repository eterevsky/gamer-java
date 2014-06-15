package players;

import gamer.Game;
import gamer.GameState;
import gamer.Move;
import gamer.Player;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MonteCarloUcb implements Player {
  private final long timeout;
  private final int MAX_SAMPLERS = 32;
  private final int SAMPLES_BATCH = 10;

  public MonteCarloUcb(long timeout) {
    this.timeout = timeout;
  }

  public <T extends Game> Move<G> selectMove(GameState<G> state)
      throws Exception {
    long startTime = System.currentTimeMillis();
    boolean iAmFirst = state.isFirstPlayersTurn();
    List<Move<G>> moves = state.getAvailableMoves();

    int[] winsByMove = new int[moves.size()];
    int[] samplesByMove = new int[moves.size()];
    Arrays.fill(winsByMove, 0);
    Arrays.fill(samplesByMove, 0);
    int total = 0;

    PriorityQueue<QueueElement<Integer>> queue = new PriorityQueue<>();
    for (int i = 0; i < moves.size(); i++) {
      queue.add(new QueuedItem(i, -1));
    }

    ExecutorService executor = Executors.newFixedThreadPool(32);
    CompletionService<Sample<Integer>> compService =
        new ExecutorCompletionService<>(executor);
    int runningSamplers = 0;

    while (System.currentTimeMillis() - startTime < timeout) {
      while (runningSamplers < MAX_SAMPLERS) {
        int imove = queue.poll().item;

        total += SAMPLES_BATCH;
        samplesByMove[imove] += SAMPLES_BATCH;
        double newPriority =
            ((double) winsByMove[imove]) /
                (samplesByMove[imove] * Math.sqrt(Math.log(total))) +
            Math.sqrt(2.0 / samplesByMove[imove]);
        queue.add(new QueuedElement<Integer>(imove, -newPriority));

        GameState<G> mcState = state.clone();
        mcState.play(moves.get(imove));
        compService.submit(
            new RandomSampler<Integer>(imove, mcState, SAMPLES_BATCH));
        runningSamplers += 1;
      }

      Sample<Integer> sample = compService.take().get();
      runningSamplers -= 1;

      int wins = (sample.result + SAMPLES_BATCH) / 2;

      if (iAmFirst) {
        winsByMove[sample.label] += wins;
      } else {
        winsByMove[sample.label] += SAMPLES_BATCH - wins;
      }
    }

    executor.shutdownNow();

    int bestMove = 0;
    for (int i = 1; i < moves.size(); i++) {
      if (winsByMove[i] > winsByMove[bestMove])
        bestMove = i;
    }

    System.out.format("%d of %d\n",
                      winsByMove[bestMove],
                      samplesByMove[bestMove]);

    return moves.get(bestMove);
  }
}
