package gamer.players;

import gamer.def.Move;
import gamer.def.Position;

public class MonteCarloUct<P extends Position<P, M>, M extends Move>
    extends GenericPlayer<P, M> {
  private int childrenThreshold = -1;

  private class Selector extends BanditSelector<P, M> {
    @Override
    public boolean shouldCreateChildren() {
      return node.getSamples() > childrenThreshold;
    }

    @Override
    public Selector newChildSelector() {
      return new Selector();
    }
  }

  @Override
  protected Node<P, M> getRoot(P position) {
    return new Node<P, M>(null, position, null, new Selector(), nodeContext);
  }

  public void setChildrenThreshold(int threshold) {
    childrenThreshold = threshold;
  }

  @Override
  public String getName() {
    if (name != null)
      return name;
    if (childrenThreshold == -1)
      childrenThreshold = samplesBatch;
    return super.getName() + String.format(" ct%d", childrenThreshold);
  }

  @Override
  public M selectMove(P position) {
    if (childrenThreshold == -1)
      childrenThreshold = samplesBatch;
    return super.selectMove(position);
  }
}
