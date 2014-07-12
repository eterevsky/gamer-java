package gamer;

import gamer.def.GameState;
import gamer.def.Move;
import gamer.def.Player;
import gamer.gomoku.Gomoku;
import gamer.players.MonteCarloUcb;
import gamer.players.MonteCarloUct;
import gamer.players.NaiveMonteCarlo;
import gamer.players.RandomPlayer;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.Random;

class App {
  public static void main(String[] args) throws Exception {
    int cores = Runtime.getRuntime().availableProcessors();
    System.out.format("Found %d cores.\n", cores);

    GameState<Gomoku> game = Gomoku.getInstance().newGame();
    Player<Gomoku> player1 = new MonteCarloUct<>();
    Player<Gomoku> player2 = new MonteCarloUct<>();
    player1.setTimeout(10);
    player2.setTimeout(10);

    System.out.println(game);

    while (!game.isTerminal()) {
      Move<Gomoku> move;
      // ExecutorService executor = Executors.newCachedThreadPool();
      if (game.getPlayer()) {
        // player1.setExecutor(executor, cores);
        move = player1.selectMove(game);
      } else {
        move = player2.selectMove(game);
      }
      // executor.shutdownNow();
      game.play(move);
      System.out.println(game);
    }
  }
}
