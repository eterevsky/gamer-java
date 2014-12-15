package gamer.players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gamer.treegame.TreeGameInstances;
import gamer.treegame.TreeGameMove;
import gamer.treegame.TreeGameState;

import java.util.ArrayList;
import java.util.List;

public final class TestNode {

  @Test(timeout=50)
  public void testLeafNode() {
    LeafNode<TreeGameState, TreeGameMove> node = new LeafNode<>(
        null, TreeGameInstances.GAME0.newGame(), null,
        new NodeContext<TreeGameState, TreeGameMove>());

    assertNull(node.getParent());
    assertNull(node.getMove());
    assertEquals(TreeGameInstances.GAME0.newGame(), node.getPosition());
    assertEquals(0, node.getSamples());
    assertEquals(0, node.getTotalSamples());

    assertTrue(node.selectChildOrAddPending(2).noChildren);
    assertEquals(0, node.getSamples());
    assertEquals(2, node.getTotalSamples());
    assertEquals(0.0, node.getPayoff(), 1E-8);

    node.addSamples(2, 0.5);
    assertEquals(2, node.getSamples());
    assertEquals(2, node.getTotalSamples());
    assertEquals(0.5, node.getPayoff(), 1E-8);

    assertTrue(node.selectChildOrAddPending(3).noChildren);
    assertTrue(node.selectChildOrAddPending(5).noChildren);
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

  private final class TestingNode
      extends BanditNode<TreeGameState, TreeGameMove> {

    private boolean willInitChildren = false;
    private TreeGameState nextSelectResult = null;

    TestingNode(Node<TreeGameState, TreeGameMove> parent,
                TreeGameState position,
                TreeGameMove move,
                NodeContext<TreeGameState, TreeGameMove> context) {
      super(parent, position, move, context);
    }

    @Override
    public boolean maybeInitChildren() {
      assertNull(children);
      if (!willInitChildren)
        return false;

      List<TreeGameMove> moves = getPosition().getMoves();
      children = new ArrayList<>(moves.size());
      for (TreeGameMove move : moves) {
        TreeGameState newPosition = getPosition().play(move);
        children.add(new TestingNode(this, newPosition, move, context));
      }

      return true;
    }

    @Override
    public TestingNode selectChild() {
      if (nextSelectResult != null) {
        for (Node<TreeGameState, TreeGameMove> child : children) {
          if (child.getPosition().equals(nextSelectResult)) {
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

  @Test(timeout=50)
  public void testParentsChildren() {
    TreeGameState pos0 = TreeGameInstances.GAME0.newGame();
    TreeGameState pos1 = pos0.play(pos0.getMoveToNode(1));
    TreeGameState pos2 = pos0.play(pos0.getMoveToNode(2));

    TestingNode root = new TestingNode(
        null, pos0, null,
        new NodeContext<TreeGameState, TreeGameMove>(false, null));

    assertTrue(root.selectChildOrAddPending(1).noChildren);
    assertEquals(1, root.getTotalSamples());
    assertEquals(0.0, root.getPayoff(), 1E-8);
    assertFalse(root.knowExact());

    root.addSamples(1, 1.0);
    assertEquals(1, root.getSamples());
    assertEquals(1.0, root.getPayoff(), 1E-8);

    root.willInitChildren = true;
    root.nextSelectResult = pos1;
    TestingNode node1 = (TestingNode) root.selectChildOrAddPending(2).child;
    assertNotNull(node1);
    assertEquals(pos1, node1.getPosition());
    assertEquals(3, root.getTotalSamples());
    assertEquals(1.0, root.getPayoff(), 1E-8);
    assertEquals(0, node1.getTotalSamples());
    node1.selectChildOrAddPending(2);
    assertEquals(2, node1.getTotalSamples());
    assertEquals(2, node1.getSamples());
    assertEquals(3, root.getSamples());

    // Bandit selectChild is used.
    TestingNode node2 = (TestingNode) root.selectChildOrAddPending(2).child;
    assertNotNull(node2);
    assertEquals(pos2, node2.getPosition());
    assertEquals(5, root.getTotalSamples());
    assertEquals(0, node2.getTotalSamples());
    node2.selectChildOrAddPending(2);
    assertEquals(2, node2.getTotalSamples());
    assertEquals(0, node2.getSamples());

    node2.addSamples(2, -1.0);
    assertEquals(2, node2.getSamples());
    assertEquals(5, root.getSamples());
    assertEquals(-1.0, node2.getPayoff(), 1E-8);
    assertEquals(0.2, root.getPayoff(), 1E-2);

    Node.SelectChildResult<TreeGameState, TreeGameMove> anotherResult =
        root.selectChildOrAddPending(1);
    assertEquals(node1, anotherResult.child);
  }

  @Test(timeout=100)
  public void testExact0() {
    TreeGameState pos0 = TreeGameInstances.GAME0.newGame();
    TreeGameState pos2 = pos0.play(pos0.getMoveToNode(2));

    TestingNode root = new TestingNode(
        null, pos0, null,
        new NodeContext<TreeGameState, TreeGameMove>(true, null));

    root.willInitChildren = true;
    root.nextSelectResult = pos2;

    TestingNode node2 = (TestingNode) root.selectChildOrAddPending(1).child;
    node2.willInitChildren = true;
    assertTrue(node2.selectChildOrAddPending(1).knowExact);
    assertTrue(node2.knowExact());
    assertEquals(-0.999, node2.getPayoff(), 1E-8);

    assertTrue(root.selectChildOrAddPending(1).knowExact);
    assertTrue(root.knowExact());
    assertEquals(0.999, root.getPayoff(), 1E-8);
  }
}
