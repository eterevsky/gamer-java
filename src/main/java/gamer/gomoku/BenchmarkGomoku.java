package gamer.gomoku;

import gamer.benchmark.Benchmark;
import gamer.def.Position;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.Random;

public class BenchmarkGomoku {
  private static double batch(Random random, int nsamples) {
    int sum = 0;
    for (int isamples = 0; isamples < nsamples; isamples++) {
      Position<?, GomokuMove> state = Gomoku.getInstance().newGame();
      while (!state.isTerminal()) {
        state.play(state.getRandomMove(random));
      }
      sum += state.getPayoff(0);
    }
    return sum;
  }

  private static double gatherResults(List<Future<Double>> futures) {
    double sum = 0;
    for (Future<Double> f : futures) {
      try {
        sum += f.get();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return sum;
  }

  @Benchmark
  public static int gomoku1mut(int reps) {
    int payoff = 0;
    Random random = new Random();

    for (int i = 0; i < reps; i++) {
      Position<?, GomokuMove> state = Gomoku.getInstance().newGame();
      while (!state.isTerminal()) {
        state.play(state.getRandomMove(random));
      }
      payoff += state.getPayoff(0);
    }

    return payoff;
  }

  @Benchmark
  public static double gomoku100kMut(int reps) {
    double sum = 0;
    Random random = new Random();

    for (int i = 0; i < reps; i++) {
      sum += batch(random, 100000);
    }

    return sum;
  }

  @Benchmark
  public static double gomoku100kMutBatches(int reps) {
    int nthreads = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(nthreads);
    final int samplesPerThread = 100000 / nthreads + 1;
    double sum = 0;

    for (int i = 0; i < reps; i++) {
      List<Future<Double>> futures = new ArrayList<>();

      for (int ithread = 0; ithread < nthreads; ithread++) {
        futures.add(executor.submit(() ->
            batch(ThreadLocalRandom.current(), samplesPerThread)));
      }

      sum += gatherResults(futures);
    }

    executor.shutdown();
    return sum;
  }

  @Benchmark
  public static double gomoku100kMutAtomicCounter(int reps) {
    double sum = 0;
    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(cores);
    AtomicInteger counter = new AtomicInteger();

    for (int i = 0; i < reps; i++) {
      List<Future<Double>> futures = new ArrayList<>();

      counter.set(0);

      for (int ithread = 0; ithread < cores; ithread++) {
        futures.add(executor.submit(new SamplerMutAtomic(counter)));
      }

      sum += gatherResults(futures);
    }

    executor.shutdown();
    return sum;
  }

  @Benchmark
  public static double gomoku100kMutSynchronizedCounter(int reps) {
    double sum = 0;
    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(cores);
    WrappedInt counter = new WrappedInt();

    for (int i = 0; i < reps; i++) {
      List<Future<Double>> futures = new ArrayList<>();
      counter.value = 0;

      for (int ithread = 0; ithread < cores; ithread++) {
        futures.add(executor.submit(new SamplerMut(counter)));
      }

      sum += gatherResults(futures);
    }

    executor.shutdown();
    return sum;
  }

  static private class SamplerMutAtomic implements Callable<Double> {
    final AtomicInteger counter;

    SamplerMutAtomic(AtomicInteger counter) {
      this.counter = counter;
    }

    @Override
    public Double call() {
      double s = 0;
      Random random = ThreadLocalRandom.current();

      while (counter.getAndIncrement() < 100000) {
        Position<?, GomokuMove> state = Gomoku.getInstance().newGame();
        while (!state.isTerminal()) {
          state.play(state.getRandomMove(random));
        }
        s += state.getPayoff(0);
      }

      return s;
    }
  }

  static class WrappedInt {
    int value;
    WrappedInt() { value = 0; }
  }

  static private class SamplerMut implements Callable<Double> {
    WrappedInt counter;

    SamplerMut(WrappedInt counter) {
      this.counter = counter;
    }

    @Override
    public Double call() {
      double s = 0;
      Random random = ThreadLocalRandom.current();

      while (true) {
        synchronized (counter) {
          if (counter.value++ > 100000)
            break;
        }
        Position<?, GomokuMove> state = Gomoku.getInstance().newGame();
        while (!state.isTerminal()) {
          state.play(state.getRandomMove(random));
        }
        s += state.getPayoff(0);
      }

      return s;
    }
  }
}
