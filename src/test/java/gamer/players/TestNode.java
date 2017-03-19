package gamer.players;

import gamer.treegame.TreeGameInstances;
import gamer.treegame.TreeGameMove;
import gamer.treegame.TreeGameState;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static gamer.players.Node.KNOW_EXACT;
import static gamer.players.Node.NO_CHILDREN;
import static org.junit.Assert.*;

public final class TestNode {

  @Test(timeout=50)
  public void testLeafNode() {
    LeafNode<TreeGameState, TreeGameMove> node = new LeafNode<>(
        null, TreeGameInstances.GAME0.newGame(), null,
        new NodeContext<TreeGameState, TreeGameMove>());

    assertNull(node.getParent());
    assertNull(node.getMove());
    assertEquals(0, node.getSamples());
    assertEquals(0, node.getTotalSamples());

    TreeGameState rootState = TreeGameInstances.GAME0.newGame();

    assertEquals(NO_CHILDREN, node.selectChildOrAddPending(rootState, 2));
    assertEquals(0, node.getSamples());
    assertEquals(2, node.getTotalSamples());
    assertEquals(0.0, node.getPayoff(), 1E-8);

    node.addSamples(2, 0.5);
    assertEquals(2, node.getSamples());
    assertEquals(2, node.getTotalSamples());
    assertEquals(0.5, node.getPayoff(), 1E-8);

    assertEquals(NO_CHILDREN, node.selectChildOrAddPending(rootState, 3));
    assertEquals(NO_CHILDREN, node.selectChildOrAddPending(rootState, 5));
    assertEquals(2, node.getSamples());
    assertEquals(10, node.getTotalSamples());
    assertEquals(0.5, node.getPayoff(), 1E-8);

    node.addSamples(5, 0.4);
    assertEquals(7, node.getSamples());
    assertEquals(10, node.getTotalSamples());
    assertEquals(3.0/7, node.getPayoff(), 1E-8);

    node.addSamples(3, 1.0/3.0);
    assertEquals(10, node.getSamples());
    assertEquals(10, node.getTotalSamples());
    assertEquals(0.4, node.getPayoff(), 1E-8);
  }

  @Test(timeout=50)
  public void testParentsChildren() {
    TreeGameState pos0 = TreeGameInstances.GAME0.newGame();
    TreeGameState pos1 = pos0.clone();
    pos1.play(pos0.getMoveToNode(1));
    TreeGameState pos2 = pos0.clone();
    pos2.play(pos0.getMoveToNode(2));

    TestingNode root = new TestingNode(
        null, pos0, null, new NodeContext<>(false, null));

    assertEquals(NO_CHILDREN, root.selectChildOrAddPending(pos0, 1));
    assertEquals(1, root.getTotalSamples());
    assertEquals(0.0, root.getPayoff(), 1E-8);
    assertFalse(root.knowExact());

    root.addSamples(1, 1.0);
    assertEquals(1, root.getSamples());
    assertEquals(1.0, root.getPayoff(), 1E-8);

    root.willInitChildren = true;
    root.nextSelectResult = pos0.getMoveToNode(1);
    TestingNode node1 = (TestingNode) root.selectChildOrAddPending(pos0, 2);
    assertNotNull(node1);
    assertEquals(3, root.getTotalSamples());
    assertEquals(1.0, root.getPayoff(), 1E-8);
    assertEquals(0, node1.getTotalSamples());
    node1.selectChildOrAddPending(pos1, 2);
    assertEquals(2, node1.getTotalSamples());
    assertEquals(2, node1.getSamples());
    assertEquals(3, root.getSamples());

    // Bandit selectChild is used.
    TestingNode node2 = (TestingNode) root.selectChildOrAddPending(pos0, 2);
    assertNotNull(node2);
    assertEquals(5, root.getTotalSamples());
    assertEquals(0, node2.getTotalSamples());
    node2.selectChildOrAddPending(pos2, 2);
    assertEquals(2, node2.getTotalSamples());
    assertEquals(0, node2.getSamples());

    node2.addSamples(2, -1.0);
    assertEquals(2, node2.getSamples());
    assertEquals(5, root.getSamples());
    assertEquals(-1.0, node2.getPayoff(), 1E-8);
    assertEquals(0.2, root.getPayoff(), 1E-2);

    assertEquals(node1, root.selectChildOrAddPending(pos1, 1));
  }

  @Test(timeout=100)
  public void testExact0() {
    TreeGameState pos0 = TreeGameInstances.GAME0.newGame();
    TreeGameState pos2 = pos0.clone();
    pos2.play(pos0.getMoveToNode(2));

    TestingNode root = new TestingNode(
        null, pos0, null, new NodeContext<>(true, null));

    root.willInitChildren = true;
    root.nextSelectResult = pos0.getMoveToNode(2);

    TestingNode node2 = (TestingNode) root.selectChildOrAddPending(pos0, 1);
    node2.willInitChildren = true;
    assertEquals(KNOW_EXACT, node2.selectChildOrAddPending(pos2, 1));
    assertTrue(node2.knowExact());
    assertEquals(-0.999, node2.getPayoff(), 1E-8);

    assertEquals(KNOW_EXACT, root.selectChildOrAddPending(pos0, 1));
    assertTrue(root.knowExact());
    assertEquals(0.999, root.getPayoff(), 1E-8);
  }

  private final class TestingNode
      extends BanditNode<TreeGameState, TreeGameMove> {

    private boolean willInitChildren = false;
    private TreeGameMove nextSelectResult = null;

    TestingNode(Node<TreeGameState, TreeGameMove> parent,
                TreeGameState position,
                TreeGameMove move,
                NodeContext<TreeGameState, TreeGameMove> context) {
      super(parent, position, move, context);
    }

    @Override
    public boolean maybeInitChildren(TreeGameState state) {
      assertNull(children);
      if (!willInitChildren)
        return false;

      List<TreeGameMove> moves = state.getMoves();
      children = new ArrayList<>(moves.size());
      for (TreeGameMove move : moves) {
        TreeGameState newState = state.clone();
        newState.play(move);
        children.add(new TestingNode(this, newState, move, context));
      }

      return true;
    }

    @Override
    public TestingNode selectChild() {
      if (nextSelectResult != null) {
        for (Node<TreeGameState, TreeGameMove> child : children) {
          if (child.getMove().equals(nextSelectResult)) {
            TestingNode result = (TestingNode) child;
            nextSelectResult = null;
            return result;
          }
        }
        throw new RuntimeException();
      }

      return (TestingNode) super.selectChild();
    }
  }
}
