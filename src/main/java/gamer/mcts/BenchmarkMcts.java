package gamer.mcts;

import gamer.benchmark.Benchmark;
import gamer.chess.Chess;
import gamer.chess.ChessMove;
import gamer.chess.ChessState;
import gamer.gomoku.Gomoku;
import gamer.gomoku.GomokuMove;
import gamer.gomoku.GomokuState;

public class BenchmarkMcts {
  @Benchmark
  public static GomokuMove mctsGomoku100kSamplesMulti(int reps) {
    int cores = Runtime.getRuntime().availableProcessors();

    MonteCarloPlayer<GomokuState, GomokuMove> player = new MonteCarloPlayer<>(Gomoku.getInstance());
    player.setMaxSamples(100000);
    player.setTimeout(-1);
    player.setSamplesBatch(1);
    player.setMaxWorkers(cores);

    GomokuMove move = null;
    for (int i = 0; i < reps; i++) {
      GomokuState s = Gomoku.getInstance().newGame();
      move = player.selectMove(s);
    }

    return move;
  }

  @Benchmark
  public static GomokuMove mctsGomoku100kSamplesHalf(int reps) {
    int cores = Runtime.getRuntime().availableProcessors();

    MonteCarloPlayer<GomokuState, GomokuMove> player = new MonteCarloPlayer<>(Gomoku.getInstance());
    player.setMaxSamples(100000);
    player.setTimeout(-1);
    player.setSamplesBatch(1);
    player.setMaxWorkers(cores / 2);

    GomokuMove move = null;
    for (int i = 0; i < reps; i++) {
      GomokuState s = Gomoku.getInstance().newGame();
      move = player.selectMove(s);
    }

    return move;
  }

  @Benchmark
  public static GomokuMove mctsGomoku100kSamplesSingle(int reps) {
    MonteCarloPlayer<GomokuState, GomokuMove> player = new MonteCarloPlayer<>(Gomoku.getInstance());
    player.setMaxSamples(100000);
    player.setTimeout(-1);
    player.setSamplesBatch(1);
    player.setMaxWorkers(1);

    GomokuMove move = null;
    for (int i = 0; i < reps; i++) {
      GomokuState s = Gomoku.getInstance().newGame();
      move = player.selectMove(s);
    }

    return move;
  }
}
