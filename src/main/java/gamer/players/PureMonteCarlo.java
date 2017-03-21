package gamer.players;

import gamer.def.Game;
import gamer.def.Move;
import gamer.def.State;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PureMonteCarlo<P extends State<P, M>, M extends Move>
    extends GenericPlayer<P, M> {

  private static class NaiveNode<P extends State<P, M>, M extends Move>
      extends Node<P, M> {

    Iterator<Node<P, M>> childrenIt = null;

    NaiveNode(P position, NodeContext<P, M> context) {
      super(null, position, null, context);
    }

    @Override
    public synchronized Node<P, M> selectChild(P state) {
      if (childrenIt == null || !childrenIt.hasNext())
        childrenIt = children.iterator();

      return childrenIt.next();
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

  public PureMonteCarlo(Game<P, M> game) {
    super(game);
  }

  @Override
  protected Node<P, M> getRoot(P position) {
    return new NaiveNode<>(position, nodeContext);
  }
}
