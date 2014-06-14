package players;

import gamer.GameState;
import gamer.Move;
import gamer.Player;

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

public class MonteCarloUct implements Player {
  private final long timeout;
  private final int MAX_SAMPLERS = 32;
  private final int SAMPLES_BATCH = 10;

  private class QueuedItem<T> implements Comparable<QueuedItem> {
    T item;
    Double priority;

    QueuedItem(T item, double priority) {
      this.item = item;
      this.node = node;
    }

    public int compareTo(QueuedItem o) {
      return priority.compareTo(o.priority);
    }
  }

  private class PositionNode {
    int samples = 0;
    int wins = 0;
    final PositionNode parent = null;
    boolean player;
    Map<Move, PositionNode> children = new HashMap<>();
    PriorityQueue<QueuedItem<Move>> queue;
  }

  public MonteCarloUct(long timeout) {
    this.timeout = timeout;
  }

  public <T extends Game> Move<T> selectMove(GameState<T> state)
      throws Exception {
    long startTime = System.currentTimeMillis();
    boolean iAmFirst = state.isFirstPlayersTurn();
    List<Move<T>> moves = state.getAvailableMoves();

    int[] winsByMove = new int[moves.size()];
    int[] samplesByMove = new int[moves.size()];
    Arrays.fill(winsByMove, 0);
    Arrays.fill(samplesByMove, 0);
    int total = 0;

    PriorityQueue<QueuedItem> queue = new PriorityQueue<>();
    for (int i = 0; i < moves.size(); i++) {
      queue.add(new QueuedItem(i, -1));
    }

    ExecutorService executor = Executors.newFixedThreadPool(32);
    CompletionService<Sample<Integer>> compService =
        new ExecutorCompletionService<>(executor);
    int runningSamplers = 0;


    while (System.currentTimeMillis() - startTime < timeout) {
      while (runningSamplers < MAX_SAMPLERS) {

...

      }

...

    }

    executor.shutdownNow();

...

    System.out.format("%d of %d\n",
                      winsByMove[bestMove],
                      samplesByMove[bestMove]);

    return bestMove;
  }
}
