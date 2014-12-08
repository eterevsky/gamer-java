package gamer;

import gamer.benchmark.Benchmark;
import gamer.benchmark.BenchmarkSuite;
import gamer.chess.BenchmarkPerft;
import gamer.gomoku.BenchmarkGomoku;
import gamer.players.BenchmarkUct;

public final class Benchmarks {
  @Benchmark
  public static long testBenchmark(int reps) {
    long total = 0;

    for (int irep = 0; irep < reps; irep++) {
      long s = 0;
      for (int i = 0; i < 10000; i++) {
        s += i;
      }
      total += s;
    }

    return total;
  }

  public static void main(String[] args) {
    BenchmarkSuite suite = new BenchmarkSuite();
    suite.add(Benchmarks.class);
    suite.add(BenchmarkGomoku.class);
    suite.add(BenchmarkPerft.class);
    suite.add(BenchmarkUct.class);

    suite.run();
  }
}
