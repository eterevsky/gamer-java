package gamer;

import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;
import gamer.gomoku.Gomoku;
import gamer.gomoku.GomokuMove;
import gamer.gomoku.GomokuState;
import gamer.players.MonteCarloUcb;
import gamer.players.MonteCarloUct;
import gamer.players.NaiveMonteCarlo;
import gamer.players.RandomPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Benchmark {
  static final List<GomokuState> testStates;

  static {
    testStates = new ArrayList<>();
    GomokuState state = Gomoku.getInstance().newGame();
    testStates.add(state);
    state = state.play(GomokuMove.create('X', 5, 5));
    testStates.add(state);
    state = state.play(GomokuMove.create('O', 5, 4));
    testStates.add(state);
    state = state.play(GomokuMove.create('X', 6, 5));
    testStates.add(state);
    state = state.play(GomokuMove.create('O', 4, 5));
    testStates.add(state);
    state = state.play(GomokuMove.create('X', 6, 3));
    testStates.add(state);
    state = state.play(GomokuMove.create('O', 6, 4));
    testStates.add(state);
    state = state.play(GomokuMove.create('X', 4, 4));
    testStates.add(state);
    state = state.play(GomokuMove.create('O', 5, 6));
    testStates.add(state);
    state = state.play(GomokuMove.create('X', 6, 6));
    testStates.add(state);
    state = state.play(GomokuMove.create('O', 3, 3));
    testStates.add(state);
    state = state.play(GomokuMove.create('X', 7, 7));
    testStates.add(state);
    state = state.play(GomokuMove.create('O', 8, 8));
    testStates.add(state);
    state = state.play(GomokuMove.create('X', 7, 5));
    testStates.add(state);
    state = state.play(GomokuMove.create('O', 7, 8));
    testStates.add(state);
    state = state.play(GomokuMove.create('X', 6, 7));
    testStates.add(state);
    state = state.play(GomokuMove.create('O', 5, 7));
    testStates.add(state);
    state = state.play(GomokuMove.create('X', 6, 8));
    testStates.add(state);
    state = state.play(GomokuMove.create('O', 6, 9));
    testStates.add(state);
    state = state.play(GomokuMove.create('X', 9, 5));
    testStates.add(state);
    state = state.play(GomokuMove.create('O', 8, 5));
    testStates.add(state);
    state = state.play(GomokuMove.create('X', 8, 6));
    testStates.add(state);
    state = state.play(GomokuMove.create('O', 5, 9));
    testStates.add(state);
  }

  static long median(List<Long> l) {
    Collections.sort(l);
    if (l.size() % 2 == 1) {
      return l.get(l.size() / 2);
    } else {
      return (l.get(l.size() / 2 - 1) + l.get(l.size() / 2)) / 2;
    }
  }

  static long mean(List<Long> l) {
    long sum = 0;
    for (long e : l) {
      sum += e;
    }

    return sum / l.size();
  }

  public static void main(String[] args) throws Exception {
    List<Long> moveTime = new ArrayList<>();

    int cores = Runtime.getRuntime().availableProcessors();
    System.out.format("Cores: %d\n", cores);
    ExecutorService executor = Executors.newFixedThreadPool(cores);

    Player<Gomoku> player = new MonteCarloUct<Gomoku>()
        .setSamplesLimit(200000)
        .setTimeout(-1)
        .setSamplesBatch(16)
        .setFindExact(false)
        .setExecutor(executor, cores);

    for (GameState<Gomoku> s : testStates) {
      long startTime = System.currentTimeMillis();
      Move<Gomoku> move = player.selectMove(s);
      moveTime.add(System.currentTimeMillis() - startTime);
      System.out.println(moveTime.get(moveTime.size() - 1));
    }

    executor.shutdownNow();
    System.out.format("Median/mean: %d / %d\n", median(moveTime), mean(moveTime));
  }
}
