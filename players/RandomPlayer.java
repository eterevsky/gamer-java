package players;

import gamer.Game;
import gamer.GameState;
import gamer.Move;
import gamer.Player;

import java.util.List;
import java.util.Random;

public class RandomPlayer implements Player {
  public <T extends Game> Move<T> selectMove(GameState<T> state) {
    return state.getRandomMove();
  }
}
