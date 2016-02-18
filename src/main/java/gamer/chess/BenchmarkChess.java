package gamer.chess;

import gamer.benchmark.Benchmark;

import java.util.List;
import java.util.Random;

public class BenchmarkChess {
  @Benchmark
  public static int chess1(int reps) {
    int payoff = 0;
    Random random = new Random();

    for (int i = 0; i < reps; i++) {
      ChessState state = Chess.getInstance().newGame();
      while (!state.isTerminal()) {
        state.playRandomMove(random);
      }
      payoff += state.getPayoff(0);
    }

    return payoff;
  }

  @Benchmark
  public static long perftInitial3(int reps) {
    ChessState state = Chess.getInstance().newGame();
    long l = 0;
    for (int i = 0; i < reps; i++) {
      l += perft(state, 3);
    }
    return l;
  }

  private static long perft(ChessState state, int depth) {
    if (depth == 1) {
      return state.getMoves().size();
    }

    long total = 0;
    for (ChessMove move : state.getMoves()) {
      ChessState newState = state.clone();
      newState.play(move);
      total += perft(newState, depth - 1);
    }

    return total;
  }
}
