package gamer.chess;

import gamer.benchmark.Benchmark;

import java.util.Arrays;
import java.util.List;

public class BenchmarkPerft {
  private static final List<String> STATES = Arrays.asList(
      "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
      "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",
      "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -",
      "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1",
      "rnbqkb1r/pp1p1ppp/2p5/4P3/2B5/8/PPP1NnPP/RNBQK2R w KQkq - 0 6",
      "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10"
  );

  private static long perft(ChessState state, int depth) {
    if (depth == 0) {
      return 1;
    }

    long total = 0;
    for (ChessMove move : state.getMoves()) {
      ChessState newState = state.clone();
      newState.play(move);
      total += perft(newState, depth - 1);
    }

    return total;
  }

  @Benchmark
  public static long timePerftInitial2(int reps) {
    ChessState state = ChessState.fromFen(STATES.get(0));
    long l = 0;
    for (int i = 0; i < reps; i++) {
      l += perft(state, 2);
    }
    return l;
  }
}