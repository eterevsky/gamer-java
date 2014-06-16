package players;

import gamer.Game;
import gamer.GameState;
import gamer.Move;
import gamer.Player;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;

public class MonteCarloUcb implements Player {
  private long timeoutInMs = 1000;
  private ExecutorService executor = null;
  private int maxWorkers = 1;

  public MonteCarloUcb() {}

  public MonteCarloUcb setTimeout(double timeoutInSec) {
    this.timeoutInMs = Math.round(1000 * timeoutInSec);
    return this;
  }

  public MonteCarloUcb setExecutor(ExecutorService executor, int maxWorkers) {
    this.executor = executor;
    this.maxWorkers = maxWorkers;
    return this;
  }

  public <G extends Game> Move<G> selectMove(GameState<G> state)
      throws Exception {
    long startTime = System.currentTimeMillis();
    boolean iAmFirst = state.getPlayer();
    List<Move<G>> moves = state.getAvailableMoves();

    int[] winsByMove = new int[moves.size()];
    int[] samplesByMove = new int[moves.size()];
    Arrays.fill(winsByMove, 0);
    Arrays.fill(samplesByMove, 0);
    int total = 0;

    PriorityQueue<QueueElement<Integer>> queue = new PriorityQueue<>();
    for (int i = 0; i < moves.size(); i++) {
      queue.add(new QueueElement<>(i, -1));
    }

    CompletionService<Sample<Integer>> compService =
        new ExecutorCompletionService<>(executor);
    int runningSamplers = 0;

    while (System.currentTimeMillis() - startTime < timeoutInMs) {
      while (runningSamplers < maxWorkers) {
        int imove = queue.poll().item;

        total += 1;
        samplesByMove[imove] += 1;
        double newPriority =
            ((double) winsByMove[imove]) /
                (samplesByMove[imove] * Math.sqrt(Math.log(total))) +
            Math.sqrt(2.0 / samplesByMove[imove]);
        queue.add(new QueueElement<>(imove, -newPriority));

        GameState<G> mcState = state.clone();
        mcState.play(moves.get(imove));
        compService.submit(
            new RandomSampler<Integer, G>(imove, mcState, 1));
        runningSamplers += 1;
      }

      Sample<Integer> sample = compService.take().get();
      runningSamplers -= 1;

      int wins = (sample.result + 1) / 2;

      if (iAmFirst) {
        winsByMove[sample.label] += wins;
      } else {
        winsByMove[sample.label] += 1 - wins;
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
