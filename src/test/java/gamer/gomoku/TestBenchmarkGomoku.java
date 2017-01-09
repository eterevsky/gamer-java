package gamer.gomoku;

import gamer.util.ConfidenceInterval;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestBenchmarkGomoku {
  private final static double EXPECTED_WINS = 0.50685;
  private final static double WINS_ERROR =  0.001;

  @Test
  public void gomokuSingle() {
    int total = 1000;
    int payoff = BenchmarkGomoku.gomokuSingle(total);
    int win = (payoff + total) / 2;
    ConfidenceInterval.Interval interval = ConfidenceInterval.binomialWilson(
        win, total - win);
    assertTrue(
        Math.abs(interval.center - EXPECTED_WINS) < 2 * interval.err + WINS_ERROR);
  }

  @Test
  public void gomokuBlockingQueue() {
    int total = 1000;
    int payoff = BenchmarkGomoku.gomokuBlockingQueue(1);
    int win = (payoff + total) / 2;
    ConfidenceInterval.Interval interval = ConfidenceInterval.binomialWilson(
        win, total - win);
    assertTrue(
        Math.abs(interval.center - EXPECTED_WINS) < interval.err + WINS_ERROR);
  }
}
