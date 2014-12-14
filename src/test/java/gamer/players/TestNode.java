package gamer.players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gamer.treegame.TreeGameInstances;
import gamer.treegame.TreeGameMove;
import gamer.treegame.TreeGameState;

import java.util.ArrayList;
import java.util.Collection;
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
    Node<TreeGameState, TreeGameMove> nodeX =
        root.selectChildOrAddPending(2).child;
    assertNotNull(nodeX);
    assertEquals(3, root.getTotalSamples());
    assertEquals(1.0, root.getPayoff(), 1E-8);
    assertEquals(0, nodeX.getTotalSamples());
    Node.SelectChildResult nodeXResult = nodeX.selectChildOrAddPending(2);
    assertEquals(2, nodeX.getTotalSamples());

    Node<TreeGameState, TreeGameMove> nodeY =
        root.selectChildOrAddPending(2).child;
    assertNotNull(nodeY);
    assertEquals(5, root.getTotalSamples());
    assertEquals(1.0, root.getPayoff(), 1E-8);
    assertEquals(0, nodeY.getTotalSamples());
    Node.SelectChildResult nodeYResult = nodeY.selectChildOrAddPending(2);
    assertEquals(2, nodeY.getTotalSamples());

    TestingNode node1, node2;
    Node.SelectChildResult node1Result, node2Result;
    if (nodeX.getPosition().equals(pos1)) {
      node1 = (TestingNode) nodeX;
      node2 = (TestingNode) nodeY;
      node1Result = nodeXResult;
      node2Result = nodeYResult;
    } else {
      node1 = (TestingNode) nodeY;
      node2 = (TestingNode) nodeX;
      node1Result = nodeYResult;
      node2Result = nodeXResult;
    }

    assertEquals(pos1, node1.getPosition());
    assertEquals(pos2, node2.getPosition());

    assertEquals(3, root.getSamples());
    assertEquals(2, node1.getSamples());
    assertEquals(2, node1.getTotalSamples());
    assertEquals(0, node2.getSamples());
    assertEquals(2, node2.getTotalSamples());

    node2.addSamples(2, -1.0);
    assertEquals(2, node2.getSamples());
    assertEquals(5, root.getSamples());
    assertEquals(-1.0, node2.getPayoff(), 1E-8);
    assertEquals(0.2, root.getPayoff(), 1E-8);

    Node.SelectChildResult anotherResult = root.selectChildOrAddPending(1);
    assertEquals(node1, anotherResult.child);
  }

  @Test
  public void testExact() {
    TreeGameState pos0 = TreeGameInstances.GAME0.newGame();
    TreeGameState pos1 = pos0.play(pos0.getMoveToNode(1));
    TreeGameState pos2 = pos0.play(pos0.getMoveToNode(2));

    TestingNode root = new TestingNode(
        null, pos0, null,
        new NodeContext<TreeGameState, TreeGameMove>(true, null));
  }
}
