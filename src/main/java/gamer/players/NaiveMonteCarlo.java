package gamer.players;

import gamer.def.Move;
import gamer.def.Position;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NaiveMonteCarlo<P extends Position<P, M>, M extends Move>
    extends GenericPlayer<P, M> {

  private static class NaiveNode<P extends Position<P, M>, M extends Move>
      extends Node<P, M> {

    Iterator<Node<P, M>> childrenIt = null;

    NaiveNode(P position, NodeContext<P, M> context) {
      super(null, position, null, context);
    }

    @Override
    public synchronized Node<P, M> selectChild() {
      if (childrenIt == null || !childrenIt.hasNext())
        childrenIt = children.iterator();

      return childrenIt.next();
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
    return new NaiveNode<>(position, nodeContext);
  }
}
