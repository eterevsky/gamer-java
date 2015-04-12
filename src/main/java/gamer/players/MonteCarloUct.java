package gamer.players;

import java.util.ArrayList;
import java.util.List;

import gamer.def.Move;
import gamer.def.Position;

public class MonteCarloUct<P extends Position<P, M>, M extends Move>
    extends GenericPlayer<P, M> {

  private static class UctNode<P extends Position<P, M>, M extends Move>
      extends BanditNode<P, M> {

    UctNode(Node<P, M> parent, P position, M move, NodeContext<P, M> context) {
      super(parent, position, move, context);
    }

    @Override
    public boolean maybeInitChildren() {
      if (getTotalSamples() < context.childrenThreshold)
        return false;

      List<M> moves = getPosition().getMoves();
      children = new ArrayList<>(moves.size());
      for (M move : moves) {
        P newPosition = getPosition().play(move);
        children.add(new UctNode<>(this, newPosition, move, context));
      }
      return true;
    }
  }

  @Override
  protected Node<P, M> getRoot(P position) {
    return new UctNode<>(null, position, null, nodeContext);
  }

  @Override
  public void setSamplesBatch(int samplesBatch) {
    this.samplesBatch = samplesBatch;
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
