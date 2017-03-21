package gamer.def;

import java.util.List;
import java.util.Random;

class GenericRandomMoveSelector<P extends State<P, M>, M extends Move>
  implements MoveSelector<P, M> {
  private Random random = new Random();

  @Override
  public M select(P position) {
    List<M> moves = position.getMoves();
    if (moves == null) {
      throw new UnsupportedOperationException(
          "Moves are not listable. Need a dedicated implementation of " +
          "playRandomMove().");
    }
    return moves.get(random.nextInt(moves.size()));
  }

  @Override
  public GenericRandomMoveSelector<P, M> clone() {
    return new GenericRandomMoveSelector<P, M>();
  }
}
