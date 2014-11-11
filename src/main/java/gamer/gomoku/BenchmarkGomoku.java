package gamer.gomoku;

import gamer.benchmark.Benchmark;
import gamer.def.GameStatus;

import java.util.Random;

public class BenchmarkGomoku {
  @Benchmark
  public static GameStatus timeRandomGame(int reps) {
    GameStatus status = null;

    for (int i = 0; i < reps; i++) {
      Random random = new Random();
      GomokuState state = new GomokuState();
      while (!state.isTerminal()) {
        state = state.play(state.getRandomMove(random));
      }
      status = state.status();
    }

    return status;
  }
}
