package gamer.players;

import gamer.benchmark.Benchmark;
import gamer.chess.Chess;
import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;
import gamer.gomoku.Gomoku;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BenchmarkUct {
  @Benchmark
  public static Move<Chess> uctChess20kSamplesMuti(int reps) {
    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(cores);

    Player<Chess> player = new MonteCarloUct<Chess>()
        .setSamplesLimit(20000)
        .setTimeout(-1)
        .setSamplesBatch(1)
        .setFindExact(true)
        .setExecutor(executor, cores);

    Move<Chess> move = null;
    for (int i = 0; i < reps; i++) {
      GameState<Chess> s = Chess.getInstance().newGame();
      move = player.selectMove(s);
    }

    executor.shutdownNow();
    return move;
  }

  @Benchmark
  public static Move<Chess> uctChess20kSamplesSingle(int reps) {
    Player<Chess> player = new MonteCarloUct<Chess>()
        .setSamplesLimit(20000)
        .setTimeout(-1)
        .setSamplesBatch(1)
        .setFindExact(true);

    Move<Chess> move = null;
    for (int i = 0; i < reps; i++) {
      GameState<Chess> s = Chess.getInstance().newGame();
      move = player.selectMove(s);
    }

    return move;
  }

  @Benchmark
  public static Move<Gomoku> uctGomoku20kSamplesMulti(int reps) {
    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(cores);

    Player<Gomoku> player = new MonteCarloUct<Gomoku>()
        .setSamplesLimit(20000)
        .setTimeout(-1)
        .setSamplesBatch(1)
        .setFindExact(true)
        .setExecutor(executor, cores);

    Move<Gomoku> move = null;
    for (int i = 0; i < reps; i++) {
      GameState<Gomoku> s = Gomoku.getInstance().newGame();
      move = player.selectMove(s);
    }

    executor.shutdownNow();
    return move;
  }

  @Benchmark
  public static Move<Gomoku> uctGomoku20kSamplesSingle(int reps) {
    Player<Gomoku> player = new MonteCarloUct<Gomoku>()
        .setSamplesLimit(20000)
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
