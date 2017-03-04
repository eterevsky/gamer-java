package gamer.gomoku;

import gamer.benchmark.Benchmark;
import gamer.def.MoveSelector;
import gamer.def.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class BenchmarkGomoku {
  static final private int CORES = Runtime.getRuntime().availableProcessors();

  @Benchmark
  public static int gomokuSingle(int reps) {
    return batch(reps);
  }

  @Benchmark
  public static int gomokuSingleSame(int nsamples) {
    int sum = 0;
    MoveSelector<GomokuState, GomokuMove> selector =
        Gomoku.getInstance().getRandomMoveSelector();
    GomokuState state = Gomoku.getInstance().newGame();
    for (int isamples = 0; isamples < nsamples; isamples++) {
      state.reset();
      while (!state.isTerminal()) {
        state.play(selector.select(state));
      }
      sum += state.getPayoff(0);
    }
    return sum;
  }

  @Benchmark
  public static int gomokuNeighbors(int nsamples) {
    int sum = 0;
    MoveSelector<GomokuState, GomokuMove> selector =
        Gomoku.getInstance().getRandomNeighborSelector();
    for (int isamples = 0; isamples < nsamples; isamples++) {
      GomokuState state = Gomoku.getInstance().newGame();
      state.play(GomokuMove.of(19 * 19 / 2));
      while (!state.isTerminal()) {
        state.play(selector.select(state));
      }
      sum += state.getPayoff(0);
    }
    return sum;
  }

  @Benchmark
  public static double gomokuBatches(int reps) {
    ExecutorService executor = Executors.newFixedThreadPool(CORES);
    List<Future<Integer>> futures = new ArrayList<>();
    int samplesLeft = reps;

    for (int ithread = 0; ithread < CORES; ithread++) {
      int samplesToThread = samplesLeft / (CORES - ithread);
      samplesLeft -= samplesToThread;
      futures.add(executor.submit(() -> batch(samplesToThread)));
    }

    executor.shutdown();
    return gatherResults(futures);
  }

  @Benchmark
  public static double gomokuAtomicCounter(int reps) {
    ExecutorService executor = Executors.newFixedThreadPool(CORES);
    List<Future<Integer>> futures = new ArrayList<>();
    final AtomicInteger counter = new AtomicInteger();
    final GomokuState.RandomSelector selector =
        Gomoku.getInstance().getRandomMoveSelector();

    for (int ithread = 0; ithread < CORES; ithread++) {
      futures.add(executor.submit(() -> {
        int s = 0;
        Random random = ThreadLocalRandom.current();

        while (counter.getAndIncrement() < reps) {
          GomokuState state = Gomoku.getInstance().newGame();
          while (!state.isTerminal()) {
            state.play(selector.select(state));
          }
          s += state.getPayoff(0);
        }

        return s;
      }));
    }

    executor.shutdown();
    return gatherResults(futures);
  }

  @Benchmark
  public static int gomokuSyncCounter(int reps) {
    ExecutorService executor = Executors.newFixedThreadPool(CORES);
    List<Future<Integer>> futures = new ArrayList<>();
    final WrappedInt counter = new WrappedInt();
    final GomokuState.RandomSelector selector =
        Gomoku.getInstance().getRandomMoveSelector();

    for (int ithread = 0; ithread < CORES; ithread++) {
      futures.add(executor.submit(() -> {
        int s = 0;
        Random random = ThreadLocalRandom.current();

        while (true) {
          synchronized (counter) {
            if (counter.value >= reps)
              break;
            counter.value++;
          }
          GomokuState state = Gomoku.getInstance().newGame();
          while (!state.isTerminal()) {
            state.play(selector.select(state));
          }
          s += state.getPayoff(0);
        }

        return s;
      }));
    }

    executor.shutdown();
    return gatherResults(futures);
  }

  @Benchmark
  public static int gomokuBlockingQueue(int reps) {
    ExecutorService executor = Executors.newFixedThreadPool(CORES);
    GomokuState initialState = Gomoku.getInstance().newGame();
    int queueLen = CORES * 2;
    final GomokuState.RandomSelector selector =
        Gomoku.getInstance().getRandomMoveSelector();

    BlockingQueue<Job> jobsQueue = new LinkedBlockingQueue<>();
    BlockingQueue<Job> resultsQueue = new LinkedBlockingQueue<>();

    for (int ithread = 0; ithread < CORES; ithread++) {
      executor.submit(() -> {
        Random random = ThreadLocalRandom.current();
        try {
          while (true) {
            Job job = jobsQueue.take();
            if (job.state == null)
              return;
            GomokuState state = job.state.clone();
            while (!state.isTerminal()) {
              state.play(selector.select(state));
            }
            job.result = state.getPayoff(0);
            resultsQueue.put(job);
          }
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      });
    }

    int sum = 0;
    try {
      for (int i = 0; i < queueLen; i++) {
        jobsQueue.put(new Job(initialState));
      }

      for (int sent = queueLen; sent < reps; sent++) {
        Job result = resultsQueue.take();
        sum += result.result;

        // Reusing previous job object.
        result.state = initialState;
        jobsQueue.put(result);
      }

      for (int i = 0; i < CORES; i++) {
        jobsQueue.put(new Job(null));
      }

      for (int i = 0; i < queueLen; i++) {
        Job result = resultsQueue.take();
        sum += result.result;
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    executor.shutdown();

    return sum;
  }

  private static int batch(int nsamples) {
    int sum = 0;
    MoveSelector<GomokuState, GomokuMove> selector =
        Gomoku.getInstance().getRandomMoveSelector();
    for (int isamples = 0; isamples < nsamples; isamples++) {
      GomokuState state = Gomoku.getInstance().newGame();
      while (!state.isTerminal()) {
        state.play(selector.select(state));
      }
      sum += state.getPayoff(0);
    }
    return sum;
  }

  private static int gatherResults(List<Future<Integer>> futures) {
    int sum = 0;
    for (Future<Integer> f : futures) {
      try {
        sum += f.get();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return sum;
  }

  static private class WrappedInt {
    int value;
    WrappedInt() { value = 0; }
  }

  static private class Job {
    GomokuState state;
    int result;

    Job(GomokuState state) {
      this.state = state;
    }
  }
}
