package gamer.players;

import gamer.benchmark.Benchmark;
import gamer.chess.Chess;
import gamer.chess.ChessMove;
import gamer.def.Player;
import gamer.gomoku.Gomoku;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BenchmarkUct {
  // @Benchmark
  public static ChessMove uctChess20kSamplesMulti(int reps) {
    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(cores);

    Player player = new MonteCarloUct()
        .setSamplesLimit(20000)
        .setTimeout(-1)
        .setSamplesBatch(1)
        .setFindExact(true)
        .setExecutor(executor, cores);

    ChessMove move = null;
    for (int i = 0; i < reps; i++) {
      ChessState s = Chess.getInstance().newGame();
      move = player.selectMove(s);
    }

    executor.shutdownNow();
    return move;
  }

  // @Benchmark
  public static ChessMove uctChess20kSamplesSingle(int reps) {
    Player<Chess> player = new MonteCarloUct()
        .setSamplesLimit(20000)
        .setTimeout(-1)
        .setSamplesBatch(1)
        .setFindExact(true);

    ChessMove move = null;
    for (int i = 0; i < reps; i++) {
      ChessState s = Chess.getInstance().newGame();
      move = player.selectMove(s);
    }

    return move;
  }

  @Benchmark
  public static GomokuMove uctGomoku200kSamplesMulti(int reps) {
    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(cores);

    Player player = new MonteCarloUct()
        .setSamplesLimit(200000)
        .setTimeout(-1)
        .setSamplesBatch(1)
        .setFindExact(true)
        .setExecutor(executor, cores);

    GomokuMove move = null;
    for (int i = 0; i < reps; i++) {
      GomokuState s = Gomoku.getInstance().newGame();
      move = player.selectMove(s);
    }

    executor.shutdownNow();
    return move;
  }

  @Benchmark
  public static GomokuMove uctGomoku200kSamplesSingle(int reps) {
    Player player = new MonteCarloUct()
        .setSamplesLimit(200000)
        .setTimeout(-1)
        .setSamplesBatch(1)
        .setFindExact(true);

    Move<Gomoku> move = null;
    for (int i = 0; i < reps; i++) {
      GameState<Gomoku> s = Gomoku.getInstance().newGame();
      move = player.selectMove(s);
    }

    return move;
  }
}
