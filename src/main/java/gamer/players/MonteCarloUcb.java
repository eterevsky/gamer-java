package gamer.players;

import gamer.def.Game;
import gamer.def.Move;
import gamer.def.State;

import java.util.ArrayList;
import java.util.List;

public class MonteCarloUcb<S extends State<S, M>, M extends Move>
    extends GenericPlayer<S, M> {

  private static class UcbNode<P extends State<P, M>, M extends Move>
      extends BanditNode<P, M> {

    UcbNode(P position, NodeContext<P, M> context) {
      super(null, position, null, context);
    }

    @Override
    public boolean maybeInitChildren(P state) {
      List<M> moves = state.getMoves();
      children = new ArrayList<>(moves.size());
      for (M move : moves) {
        P newState = state.clone();
        newState.play(move);
        children.add(new LeafNode<>(this, newState, move, context));
      }

      return true;
    }
  }

  public MonteCarloUcb(Game<S, M> game) {
    super(game);
  }

  @Override
  protected Node<S, M> getRoot(S position) {
    return new UcbNode<>(position, nodeContext);
  }
}
