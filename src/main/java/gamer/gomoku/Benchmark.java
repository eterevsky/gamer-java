package gamer.gomoku;

import java.util.Random;

public class Benchmark {
  class MyRandom extends Random {
    public int nextInt(int n) {
      rng_state_ *= 987654323;
      rng_state_ += 555555587;
      rng_state_ %= 2000000011;
      return (int)rng_state_ % n;
    }

    private long rng_state_;
  }

  public static void main(String[] args) throws Exception {
    Random random = new Random();
    for (int i = 0; i < 1000000; i++) {
      GomokuState state = new GomokuState();
      while (!state.isTerminal()) {
        state = state.play(state.getRandomMove(random));
      }
    }
  }
}
