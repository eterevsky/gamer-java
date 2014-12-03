package gamer.benchmark;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class BenchmarkSuite {
  private List<Class<?>> classes = new ArrayList<>();

  public void add(Class<?> c) {
    classes.add(c);
  }

  public void run() {
    for (Class<?> c : classes) {
      for (Method m : c.getDeclaredMethods()) {
        if (m.getAnnotation(Benchmark.class) != null) {
          runBenchmark(m);
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

  static final double STUDENT_95 = 12.71;
  static final double STUDENT_99 = 63.66;

  private static Stats genStats(List<Double> samples) {
    Stats stats = new Stats();
    stats.mean = 0;
    for (double s : samples) {
      stats.mean += s;
    }

    stats.mean /= samples.size();

    if (samples.size() < 2) {
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

  private static void runBenchmark(Method benchmark) {
    try {
      int reps = 1;

      while (reps < 1000000000 && singleRun(benchmark, reps) < 0.3) {
        reps *= 2;
      }

      singleRun(benchmark, reps);  // warmup
      List<Double> times = new ArrayList<>();
      long startTime = System.nanoTime();

      while (times.size() < 6 ||
             (System.nanoTime() - startTime < 3E11 &&
              genStats(times).error() > 0.05)) {
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

  private static void printResults(Method benchmark, List<Double> samples) {
    Stats stats = genStats(samples);
    double scale = 1.0;
    String unit = "s";

    if (stats.mean >= 2.0) {
      scale = 1.0;
      unit = "s";
    } else if (stats.mean >= 0.002) {
      scale = 1000.0;
      unit = "ms";
    } else if (stats.mean >= 2E-6) {
      scale = 1E6;
      unit = "µs";
    } else {
      scale = 1E9;
      unit = "ns";
    }

    String fullName = benchmark.getDeclaringClass().getSimpleName() + "." +
                      benchmark.getName();

    System.out.format(
        "%-50s %.1f±%.1f %s (%.1f%%)\n",
        fullName,
        stats.mean * scale,
        stats.interval * scale,
        unit,
        stats.interval / stats.mean * 100);
  }
}
