package gamer.players;

import gamer.def.Game;
import gamer.def.Move;
import gamer.def.State;

import java.util.ArrayList;
import java.util.List;

public class MonteCarloUct<S extends State<S, M>, M extends Move>
    extends GenericPlayer<S, M> {

  private static class UctNode<P extends State<P, M>, M extends Move>
      extends BanditNode<P, M> {

    UctNode(Node<P, M> parent, P position, M move, NodeContext<P, M> context) {
      super(parent, position, move, context);
    }

    @Override
    public boolean maybeInitChildren(P state) {
      if (getTotalSamples() < context.childrenThreshold)
        return false;

      List<M> moves = state.getMoves();
      children = new ArrayList<>(moves.size());
      for (M move : moves) {
        P newState = state.clone();
        newState.play(move);
        children.add(new UctNode<>(this, newState, move, context));
      }
      return true;
    }
  }

  public MonteCarloUct(Game<S, M> game) {
    super(game);
  }

  @Override
  protected Node<S, M> getRoot(S position) {
    return new UctNode<>(null, position, null, nodeContext);
  }

  @Override
  public void setSamplesBatch(int samplesBatch) {
    super.setSamplesBatch(samplesBatch);
    if (nodeContext.childrenThreshold < samplesBatch) {
      nodeContext.childrenThreshold = samplesBatch;
    }
  }

  public void setChildrenThreshold(int threshold) {
    if (threshold < 1) {
      throw new RuntimeException("Children threshold should be at least 1");
    }
    nodeContext.childrenThreshold = threshold;
  }

  @Override
  public String getName() {
    if (name != null)
      return name;
    return super.getName() +
           String.format(" ct%d", nodeContext.childrenThreshold);
  }
}
