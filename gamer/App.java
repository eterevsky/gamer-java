package gamer;

import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;
import gamer.gomoku.Gomoku;
import gamer.players.MonteCarloUcb;
import gamer.players.MonteCarloUct;
import gamer.players.NaiveMonteCarlo;
import gamer.players.RandomPlayer;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

class App {
  public static void main(String[] args) throws Exception {
    int cores = Runtime.getRuntime().availableProcessors();
    System.out.format("Found %d cores.\n", cores);

    GameState<Gomoku> game = Gomoku.getInstance().newGame();
    Player<Gomoku> player1 = new MonteCarloUct<>();
    Player<Gomoku> player2 = new MonteCarloUct<>();
    ExecutorService executor = Executors.newFixedThreadPool(cores);
    player1.setTimeout(5000).setExecutor(executor, cores).setSamplesBatch(8);
    player2.setTimeout(5000).setExecutor(executor, cores).setSamplesBatch(16);

    System.out.println(game);

    while (!game.isTerminal()) {
      Move<Gomoku> move;
      if (game.status().getPlayer()) {
        move = player1.selectMove(game);
      } else {
        move = player2.selectMove(game);
      }
      game = game.play(move);
      System.out.println(game);
    }

    executor.shutdown();
  }
}
