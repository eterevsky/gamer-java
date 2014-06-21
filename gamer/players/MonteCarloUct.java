package gamer.players;

import gamer.def.Game;
import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MonteCarloUct<G extends Game> implements Player<G> {
  private final int MAX_SAMPLERS = 32;
  private final int SAMPLES_BATCH = 10;

  private long timeoutInMs;
  private ExecutorService executor = null;
  private int maxWorkers = 1;

  private class PositionNode<G extends Game> {
    int samples = 0;
    int wins = 0;
    final PositionNode parent;
    final boolean player;
    Map<Move<G>, PositionNode> children = new HashMap<>();
    private PriorityQueue<QueueElement<Move<G>>> queue = new PriorityQueue<>();

    PositionNode(PositionNode parent, boolean player, List<Move<G>> moves) {
      this.parent = parent;
      this.player = player;
      for (Move<G> move : moves) {
        this.children.put(move, null);
        this.queue.add(new QueueElement<Move<G>>(move, -1));
      }
    }
  }

  private class NodeAndState<G extends Game> {
    PositionNode<G> node;
    GameState<G> state;
  }

  public MonteCarloUct() {}

  public MonteCarloUct setTimeout(double timeoutInSec) {
    this.timeoutInMs = Math.round(1000 * timeoutInSec);
    return this;
  }

  public MonteCarloUct setExecutor(ExecutorService executor, int maxWorkers) {
    this.executor = executor;
    this.maxWorkers = maxWorkers;
    return this;
  }

  public Move<G> selectMove(GameState<G> state) throws Exception {
    long startTime = System.currentTimeMillis();

    PositionNode<G> root =
        new PositionNode<>(null, state.getPlayer(), state.getAvailableMoves());

    ExecutorService executor = Executors.newFixedThreadPool(32);
    CompletionService<Sample<Integer>> compService =
        new ExecutorCompletionService<>(executor);
    int runningSamplers = 0;

/*    while (System.currentTimeMillis() - startTime < timeout) {
      if (runningSamplers < MAX_SAMPLERS) {
        NodeAndState<G> = getNodeFromQueue(root, state);

      }

...

    }

    executor.shutdownNow();

...

    System.out.format("%d of %d\n",
                      winsByMove[bestMove],
                      samplesByMove[bestMove]);

    return bestMove;
*/
    return null;
  }
}
