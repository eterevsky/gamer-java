package gamer.gomoku;

import gamer.benchmark.Benchmark;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.Random;

public class BenchmarkGomoku {
  @Benchmark
  public static int gomoku1(int reps) {
    int payoff = 0;

    for (int i = 0; i < reps; i++) {
      Random random = new Random();
      GomokuState state = Gomoku.getInstance().newGame();
      while (!state.isTerminal()) {
        state = state.play(state.getRandomMove(random));
      }
      payoff += state.getPayoff(0);
    }

    return payoff;
  }

  @Benchmark
  public static int gomoku1mut(int reps) {
    int payoff = 0;

    for (int i = 0; i < reps; i++) {
      Random random = new Random();
      GomokuStateMut state = Gomoku.getInstance().newGameMut();
      while (!state.isTerminal()) {
        state.apply(state.getRandomMove(random));
      }
      payoff += state.getPayoff(0);
    }

    return payoff;
  }

  @Benchmark
  public static double gomoku100k(int reps) {
    double sum = 0;
    Random random = new Random();

    for (int i = 0; i < reps; i++) {
      for (int isamples = 0; isamples < 100000; isamples++) {
        GomokuState state = Gomoku.getInstance().newGame();
        while (!state.isTerminal()) {
          state = state.play(state.getRandomMove(random));
        }
        sum += state.getPayoff(0);
      }
    }

    return sum;
  }

  @Benchmark
  public static double gomoku100kMut(int reps) {
    double sum = 0;
    Random random = new Random();

    for (int i = 0; i < reps; i++) {
      GomokuStateMut state = Gomoku.getInstance().newGameMut();
      for (int isamples = 0; isamples < 100000; isamples++) {
        state.reset();
        while (!state.isTerminal()) {
          state.apply(state.getRandomMove(random));
        }
        sum += state.getPayoff(0);
      }
    }

    return sum;
  }

  @Benchmark
  public static double gomoku100kBatches(int reps) {
    double sum = 0;
    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(cores);
    final int samplesByThread = 100000 / cores + 1;

    for (int i = 0; i < reps; i++) {
      List<Future<Double>> futures = new ArrayList<>();

      for (int ithread = 0; ithread < cores; ithread++) {
        futures.add(executor.submit(new Callable<Double>() {
          @Override
          public Double call() {
            double s = 0;
            Random random = ThreadLocalRandom.current();

            for (int isamples = 0; isamples < samplesByThread; isamples++) {
              GomokuState state = Gomoku.getInstance().newGame();
              while (!state.isTerminal()) {
                state = state.play(state.getRandomMove(random));
              }
              s += state.getPayoff(0);
            }

            return s;
          }
        }));
      }

      for (Future<Double> f : futures) {
        try {
          sum += f.get();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }

    executor.shutdown();
    return sum;
  }

  static class WrappedInt {
    WrappedInt() { value = 0; }
    int value;
  }

  @Benchmark
  public static double gomoku100kCounter(int reps) {
    double sum = 0;
    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(cores);
    final WrappedInt counter = new WrappedInt();

    for (int i = 0; i < reps; i++) {
      List<Future<Double>> futures = new ArrayList<>();

      for (int ithread = 0; ithread < cores; ithread++) {
        futures.add(executor.submit(new Callable<Double>() {
          @Override
          public Double call() {
            double s = 0;
            Random random = ThreadLocalRandom.current();

            while (counter.value < 100000) {
              counter.value += 1;
              GomokuState state = Gomoku.getInstance().newGame();
              while (!state.isTerminal()) {
                state = state.play(state.getRandomMove(random));
              }
              s += state.getPayoff(0);
            }

            return s;
          }
        }));
      }

      for (Future<Double> f : futures) {
        try {
          sum += f.get();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }

    executor.shutdown();
    return sum;
  }

  @Benchmark
  public static double gomoku100kMutCounter(int reps) {
    double sum = 0;
    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(cores);
    final WrappedInt counter = new WrappedInt();

    for (int i = 0; i < reps; i++) {
      List<Future<Double>> futures = new ArrayList<>();

      for (int ithread = 0; ithread < cores; ithread++) {
        futures.add(executor.submit(new Callable<Double>() {
          @Override
          public Double call() {
            double s = 0;
            Random random = ThreadLocalRandom.current();

            while (counter.value < 100000) {
              counter.value += 1;
              GomokuStateMut state = Gomoku.getInstance().newGameMut();
              while (!state.isTerminal()) {
                state.apply(state.getRandomMove(random));
              }
              s += state.getPayoff(0);
            }

            return s;
          }
        }));
      }

      for (Future<Double> f : futures) {
        try {
          sum += f.get();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }

    executor.shutdown();
    return sum;
  }
}
