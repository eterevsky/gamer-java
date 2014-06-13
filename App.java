import gamer.GameState;
import gomoku.Gomoku;

class App {
  public static void main(String[] args) {
    GameState game = Gomoku.newGame();
    System.out.println(game);
  }
}
