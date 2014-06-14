import gamer.GameState;
import gamer.Move;
import gamer.Player;
import gomoku.Gomoku;
import players.RandomPlayer;
import players.NaiveMonteCarlo;

class App {
  public static void main(String[] args) throws Exception {
    GameState<Gomoku> game = Gomoku.newGame();
    Player player1 = new NaiveMonteCarlo(5000);
    Player player2 = new NaiveMonteCarlo(6000);

    System.out.println(game);

    while (!game.isTerminal()) {
      Move<Gomoku> move;
      if (game.isFirstPlayersTurn()) {
        move = player1.selectMove(game);
      } else {
        move = player2.selectMove(game);
      }
      game.play(move);
      System.out.println(game);
    }
  }
}
