package gamer;

import java.util.List;
import java.util.Random;

public class RandomPlayer implements Player {
  public <T extends Game> Move<T> selectMove(GameState<T> state) {
    List<Move<T>> moves = state.getAvailableMoves();
    return moves.get(new Random().nextInt(moves.size()));
  }
}
