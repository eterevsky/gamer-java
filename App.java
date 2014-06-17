import gamer.GameState;
import gamer.Move;
import gamer.Player;
import gomoku.Gomoku;
import players.MonteCarloUcb;
import players.NaiveMonteCarlo;
import players.RandomPlayer;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

class App {
  public static void main(String[] args) throws Exception {
    int cores = Runtime.getRuntime().availableProcessors();
    System.out.format("Found %d cores.\n", cores);
    ExecutorService executor = Executors.newCachedThreadPool();

    GameState<Gomoku> game = Gomoku.newGame();
    Player<Gomoku> player1 = new NaiveMonteCarlo<>();
    Player<Gomoku> player2 = new MonteCarloUcb<>();
    player1.setExecutor(executor, cores).setTimeout(2);
    player2.setExecutor(executor, cores).setTimeout(2);

    System.out.println(game);

    while (!game.isTerminal()) {
      Move<Gomoku> move;
      if (game.getPlayer()) {
        move = player1.selectMove(game);
      } else {
        move = player2.selectMove(game);
      }
      game.play(move);
      System.out.println(game);
    }

    executor.shutdownNow();
  }
}
