package gamer.gomoku;

import gamer.benchmark.Benchmark;
import gamer.def.GameStatus;

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
  public static GameStatus timeRandomGame(int reps) {
    GameStatus status = null;

    for (int i = 0; i < reps; i++) {
      Random random = new Random();
      GomokuState state = Gomoku.getInstance().newGame();
      while (!state.isTerminal()) {
        state = state.play(state.getRandomMove(random));
      }
      status = state.status();
    }

    return status;
  }

  @Benchmark
  public static GameStatus timeRandomGameMut(int reps) {
    GameStatus status = null;

    for (int i = 0; i < reps; i++) {
      Random random = new Random();
      GomokuStateMut state = Gomoku.getInstance().newGameMut();
      while (!state.isTerminal()) {
        state.playInPlace(state.getRandomMove(random));
      }
      status = state.status();
    }

    return status;
  }

  @Benchmark
  public static double averageOver200k(int reps) {
    double sum = 0;
    Random random = new Random();

    for (int i = 0; i < reps; i++) {
      for (int isamples = 0; isamples < 200000; isamples++) {
        GomokuState state = Gomoku.getInstance().newGame();
        while (!state.isTerminal()) {
          state = state.play(state.getRandomMove(random));
        }
        sum += state.status().value();
      }
    }

    return sum;
  }

  @Benchmark
  public static double averageOver200kMut(int reps) {
    double sum = 0;
    Random random = new Random();

    for (int i = 0; i < reps; i++) {
      GomokuStateMut state = Gomoku.getInstance().newGameMut();
      for (int isamples = 0; isamples < 200000; isamples++) {
        state.reset();
        while (!state.isTerminal()) {
          state.playInPlace(state.getRandomMove(random));
        }
        sum += state.status().value();
      }
    }

    return sum;
  }

  @Benchmark
  public static double averageOver200kMulti(int reps) {
    double sum = 0;
    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(cores);
    final int samplesByThread = 200000 / cores + 1;

    for (int i = 0; i < reps; i++) {
      List<Future<Double>> futures = new ArrayList<>();

      for (int ithread = 0; ithread < cores; ithread++) {
        futures.add(executor.submit(new Callable<Double>() {
          public Double call() {
            double s = 0;
            Random random = ThreadLocalRandom.current();

            for (int isamples = 0; isamples < samplesByThread; isamples++) {
              GomokuState state = Gomoku.getInstance().newGame();
              while (!state.isTerminal()) {
                state = state.play(state.getRandomMove(random));
              }
              s += state.status().value();
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
  public static double averageOver200kCommonCounter(int reps) {
    double sum = 0;
    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(cores);
    final WrappedInt counter = new WrappedInt();

    for (int i = 0; i < reps; i++) {
      List<Future<Double>> futures = new ArrayList<>();

      for (int ithread = 0; ithread < cores; ithread++) {
        futures.add(executor.submit(new Callable<Double>() {
          public Double call() {
            double s = 0;
            Random random = ThreadLocalRandom.current();

            while (counter.value < 200000) {
              counter.value += 1;
              GomokuState state = Gomoku.getInstance().newGame();
              while (!state.isTerminal()) {
                state = state.play(state.getRandomMove(random));
              }
              s += state.status().value();
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
