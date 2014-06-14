import gamer.GameState;
import gamer.Move;
import gamer.Player;
import gamer.RandomPlayer;
import gomoku.Gomoku;

class App {
  public static void main(String[] args) throws Exception {
    GameState<Gomoku> game = Gomoku.newGame();
    Player player = new RandomPlayer();

    System.out.println(game);

    while (!game.isTerminal()) {
      Move<Gomoku> move = player.selectMove(game);
      game.play(move);
      System.out.println(game);
    }
  }
}
