package gamer.players;

import java.util.ArrayList;
import java.util.List;

import gamer.def.Move;
import gamer.def.Position;

public class MonteCarloUcb<P extends Position<P, M>, M extends Move>
    extends GenericPlayer<P, M> {

  private static class UcbNode<P extends Position<P, M>, M extends Move>
      extends BanditNode<P, M> {

    UcbNode(P position, NodeContext<P, M> context) {
      super(null, position, null, context);
    }

    @Override
    public boolean maybeInitChildren() {
      List<M> moves = getState().getMoves();
      children = new ArrayList<>(moves.size());
      for (M move : moves) {
        P newState = getState().clone();
        newState.play(move);
        children.add(new LeafNode<>(this, newState, move, context));
      }

      return true;
    }
  }

  @Override
  protected Node<P, M> getRoot(P position) {
    return new UcbNode<>(position, nodeContext);
  }
}
