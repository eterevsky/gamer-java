package gamer.g2048;

import gamer.benchmark.Benchmark;

public class Benchmark2048 {
  @Benchmark
  public static int g2048(int reps) {
    long payoff = 0;
    for (int i = 0; i < reps; i++) {
      G2048State state = G2048.getInstance().newGame();
      while (!state.isTerminal()) {
        G2048Move move = state.getRandomMove();
        state.play(move);
      }
      payoff += state.getPayoff(0);
    }

    return (int)payoff;
  }
}
