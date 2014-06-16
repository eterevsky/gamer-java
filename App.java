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
    ExecutorService executor = Executors.newCachedThreadPool();

    GameState<Gomoku> game = Gomoku.newGame();
    Player player1 = new NaiveMonteCarlo();
    Player player2 = new NaiveMonteCarlo().setExecutor(executor, 1);

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
