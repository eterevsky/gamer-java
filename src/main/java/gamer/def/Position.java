package gamer.def;

import java.util.List;
import java.util.Random;

public interface Position<P extends Position<P, M>, M extends Move> {
  // -1 means random player for games with randomness.
  int getPlayer();

  boolean isTerminal();

  // Only for terminal state. 0 means draw.
  int getPayoff(int player);

  List<M> getMoves();

  M getRandomMove(Random random);

  P play(M move);

  String moveToString(M move);

  M parseMove(String moveStr);
}
