package gamer.benchmark;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class BenchmarkSuite {
  private static final double STUDENT_95 = 12.71;

  private List<Class<?>> classes = new ArrayList<>();

  private final long timeLimitSeconds;
  private final String filter;
  private double precision = 0.05;

  public BenchmarkSuite(int timeLimitSeconds, String filter) {
    this.timeLimitSeconds = timeLimitSeconds;
    this.filter = filter;
  }

  public void setPrecision(double precision) {
    this.precision = precision;
  }

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

  public void add(Class<?> c) {
    classes.add(c);
  }

  public void run() {
    System.out.format(
        "Running benchmarks.\nCores: %d\nJVM: %s %s\nTime limit: %d s\n",
        Runtime.getRuntime().availableProcessors(),
        System.getProperty("java.vm.name"), System.getProperty("java.version"),
        timeLimitSeconds);
    System.out.println();
    for (Class<?> c : classes) {
      for (Method m : c.getDeclaredMethods()) {
        if (m.getAnnotation(Benchmark.class) != null) {
          String name =
              m.getDeclaringClass().getSimpleName() + "." + m.getName();
          if (filter == null || name.contains(filter)) {
            runBenchmark(m);
          }
        }
      }
    }
  }

  static class Stats {
    double mean;
    double variance;
    double interval;

    double error() {
      return interval / mean;
    }
  }

  private static Stats genStats(List<Double> samples) {
    Stats stats = new Stats();
    stats.mean = 0;
    for (double s : samples) {
      stats.mean += s;
    }

    stats.mean /= samples.size();

    if (samples.size() == 0)
      return null;

    if (samples.size() == 1) {
      stats.variance = stats.mean;
      stats.interval = stats.mean;
      return stats;
    }

    stats.variance = 0;
    for (double s : samples) {
      stats.variance += (s - stats.mean) * (s - stats.mean);
    }
    stats.variance = Math.sqrt(stats.variance / (samples.size() - 1));

    stats.interval = STUDENT_95 * stats.variance / Math.sqrt(samples.size());
    return stats;
  }

  private void runBenchmark(Method benchmark) {
    try {
      long startTime = System.nanoTime();

      int reps = 1;
      while (reps < 1000000000 && singleRun(benchmark, reps) < 0.3) {
        reps *= 2;
      }

      List<Double> times = new ArrayList<>();

      while (System.nanoTime() - startTime < timeLimitSeconds * 1000000000 &&
             (times.size() < 6 ||
              genStats(times).error() > precision)) {
        double t = singleRun(benchmark, reps);
        times.add(t / reps);
      }

      printResults(benchmark, times);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static double singleRun(Method benchmark, int reps)
      throws IllegalAccessException, InvocationTargetException {
    long startTime = System.nanoTime();
    benchmark.invoke(null, reps);
    return (System.nanoTime() - startTime) / 1E9;
  }

  private void printResults(Method benchmark, List<Double> samples) {
    String fullName = benchmark.getDeclaringClass().getSimpleName() + "." +
                      benchmark.getName();

    Stats stats = genStats(samples);

    if (stats == null) {
      System.out.format("%-50s >%ds\n", fullName, timeLimitSeconds);
      return;
    }

    double scale = 1.0;
    String unit = "s";

    if (stats.mean < 2E-6) {
      scale = 1E9;
      unit = "ns";
    } else if (stats.mean < 0.002) {
      scale = 1E6;
      unit = "us";
    } else if (stats.mean < 2.0) {
      scale = 1000.0;
      unit = "ms";
    }

    if (stats.interval == stats.mean) {
      System.out.format("%-50s %.1f±?\n", fullName, stats.mean * scale);
    } else {
      System.out.format(
          "%-50s %6.1f +- %5.1f %s (%4.1f%%)\n",
          fullName,
          stats.mean * scale,
          stats.interval * scale,
          unit,
          stats.interval / stats.mean * 100);
    }
  }
}
