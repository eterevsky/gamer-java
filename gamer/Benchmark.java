package gamer;

import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;
import gamer.gomoku.Gomoku;
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
  static long median(List<Long> l) {
    Collections.sort(l);
    if (l.size() % 2 == 1) {
      return l.get(l.size() / 2);
    } else {
      return (l.get(l.size() / 2 - 1) + l.get(l.size() / 2)) / 2;
    }
  }

  public static void main(String[] args) throws Exception {
    List<Long> moveTime = new ArrayList<>();

    Random random = new Random(1234567890L);
    int cores = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(cores);
    GameState<Gomoku> game = Gomoku.getInstance().newGame();

    Player<Gomoku> player = new MonteCarloUct<>();
    player.setSamplesLimit(200000)
          .setTimeout(-1)
          .setRandom(random)
          .setExecutor(executor, cores);

    while (!game.isTerminal()) {
      long startTime = System.currentTimeMillis();
      Move<Gomoku> move = player.selectMove(game);
      moveTime.add(System.currentTimeMillis() - startTime);
      game = game.play(move);
      System.out.println(game);
    }

    executor.shutdown();
    System.out.println("Median: " + median(moveTime));
  }
}
