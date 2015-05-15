package gamer.gomoku;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestBenchmarkGomoku {
  @Test
  public void gomoku1() {
    double res = BenchmarkGomoku.gomoku1(10);
    assertTrue(res >= -10);
    assertTrue(res <= 10);
  }

  @Test
  public void gomoku100kQueue() {
    double res = BenchmarkGomoku.gomoku100kQueue(1);
  }
}