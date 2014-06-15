package players;

import gamer.Game;
import gamer.GameState;
import gamer.Move;
import gamer.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class NaiveMonteCarlo implements Player {
  private double timeoutInSec;
  private Executor executor = null;
  private int maxWorkers;

  public NaiveMonteCarlo() {}

  public NaiveMonteCarlo setTimeout(double timeoutInSec) {
    this.timeoutInSec = timeoutInSec;
    return this;
  }

  public MonteCarloUcb setExecutor(Executor executor, int maxWorkers) {
    this.executor = executor;
    this.maxWorkers = maxWorkers;
    return this;
  }

  public <G extends Game> Move<G> selectMove(GameState<G> state)
      throws Exception {
    long startTime = System.currentTimeMillis();

    List<Move<G>> moves = state.getAvailableMoves();
    int[] resultByMove = new int[moves.size()];
    int[] totalByMove = new int[moves.size()];


    GameState<G>[] stateAfterMove = new ...

    Arrays.fill(resultByMove, 0);
    Arrays.fill(totalByMove, 0);
    boolean iAmFirst = state.getPlayer();

    EvaluationQueue<G, Integer> evaluationQueue = new EvaluationQueue<>(
        new RandomSampleEvaluator(), executor, maxWorkers);

    int imove = 0;

    while (System.currentTimeMillis() - startTime < timeoutInSec * 1000) {
      while (evaluationQueue.needMoreWork()) {
        evaluationQueue.put(imove,
      }

      while (runningSamplers < MAX_SAMPLERS) {
        GameState<G> mcState = state.clone();
        mcState.play(moves.get(imove));
        compService.submit(new RandomSampler<Integer, G>(imove, mcState, 10));
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
