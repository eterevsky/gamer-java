package gamer.players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import gamer.def.GameStatus;
import gamer.treegame.TreeGame;
import gamer.treegame.TreeGameInstances;
import gamer.treegame.TreeGameState;

import org.junit.Test;

public final class TestNode {

  @Test(timeout=50)
  public void testNoChildren() {
    Node<TreeGame> node = new Node<TreeGame>(
        null,
        TreeGameInstances.GAME0.newGame(),
        null,
        new LeafSelector<TreeGame>(),
        NodeContext.BASIC);

    assertNull(node.getParent());
    assertNull(node.getMove());
    assertEquals(TreeGameInstances.GAME0.newGame(), node.getState());
    assertEquals(0, node.getSamples());
    assertEquals(0, node.getTotalSamples());

    assertNull(node.selectChildOrAddPending(2));
    assertEquals(0, node.getSamples());
    assertEquals(2, node.getTotalSamples());
    assertEquals(0.0, node.getValue(), 1E-8);

    node.addSamples(2, 0.5);
    assertEquals(2, node.getSamples());
    assertEquals(2, node.getTotalSamples());
    assertEquals(0.5, node.getValue(), 1E-8);

    assertNull(node.selectChildOrAddPending(3));
    assertNull(node.selectChildOrAddPending(5));
    assertEquals(2, node.getSamples());
    assertEquals(10, node.getTotalSamples());
    assertEquals(0.1, node.getValue(), 1E-8);

    node.addSamples(5, 0.4);
    assertEquals(7, node.getSamples());
    assertEquals(10, node.getTotalSamples());
    assertEquals(0.3, node.getValue(), 1E-8);

    node.addSamples(3, 1.0/3.0);
    assertEquals(10, node.getSamples());
    assertEquals(10, node.getTotalSamples());
    assertEquals(0.4, node.getValue(), 1E-8);
  }

  final class TestSelector implements Node.Selector<TreeGame> {
    private Node<TreeGame> node;
    TreeGameState selectResultState = null;
    boolean shouldCreateChildrenResult = false;
    List<TestSelector> childSelectors = new ArrayList<>();
    int childUpdatedCalls = 0;

    public void setNode(Node<TreeGame> node) {
      this.node = node;
    }

    public Node<TreeGame> select(Collection<Node<TreeGame>> children,
                          long totalSamples) {
      assertNotNull(selectResultState);
      Node<TreeGame> result = node.getChildByStateForTest(selectResultState);
      selectResultState = null;
      return result;
    }

    public boolean shouldCreateChildren() {
      return shouldCreateChildrenResult;
    }

    public TestSelector newChildSelector() {
      TestSelector selector = new TestSelector();
      childSelectors.add(selector);
      return selector;
    }

    public void childUpdated(Node<TreeGame> child, long totalSamples) {
      childUpdatedCalls += 1;
    }

    TestSelector getChildSelector(Node<TreeGame> child) {
      for (TestSelector childSelector : childSelectors) {
        if (childSelector.node == child)
          return childSelector;
      }
      return null;
    }
  }

  @Test(timeout=50)
  public void testParentsChildren() {
    TestSelector rootSelector = new TestSelector();
    TreeGameState rootState = TreeGameInstances.GAME0.newGame();

    Node<TreeGame> root = new Node<TreeGame>(
        null, rootState, null, rootSelector, NodeContext.BASIC);

    assertNull(root.selectChildOrAddPending(1));
    assertEquals(1, root.getTotalSamples());
    assertEquals(0.0, root.getValue(), 1E-8);
    assertFalse(root.knowExactValue());

    TreeGameState state1 = rootState.play(rootState.getMoveToNode(1));
    TreeGameState state2 = rootState.play(rootState.getMoveToNode(2));

    root.addSamples(1, 1.0);
    assertEquals(1, root.getSamples());
    assertEquals(1.0, root.getValue(), 1E-8);

    // assertEquals(0, rootSelector.childUpdatedCalls);

    // Test sampling node 2.

    rootSelector.shouldCreateChildrenResult = true;
    rootSelector.selectResultState = state2;
    Node<TreeGame> node2 = root.selectChildOrAddPending(2);
    assertEquals(1, root.getSamples());
    assertEquals(3, root.getTotalSamples());
    assertEquals(1.0/3.0, root.getValue(), 1E-8);

    assertNotNull(node2);
    assertEquals(state2, node2.getState());
    assertEquals(root, node2.getParent());
    assertEquals(rootState.getMoveToNode(2), node2.getMove());
    assertEquals(0, node2.getSamples());
    assertEquals(0, node2.getTotalSamples());

    // int oldChildUpdates = rootSelector.childUpdatedCalls;

    assertNull(node2.selectChildOrAddPending(2));
    assertFalse(node2.knowExactValue());
    assertEquals(0, node2.getSamples());
    assertEquals(2, node2.getTotalSamples());
    assertEquals(0.0, node2.getValue(), 1E-8);

    assertFalse(root.knowExactValue());
    assertEquals(1, root.getSamples());
    assertEquals(3, root.getTotalSamples());
    assertEquals(1.0/3.0, root.getValue(), 1E-8);

    // assertTrue(rootSelector.childUpdatedCalls > oldChildUpdates);

    // oldChildUpdates = rootSelector.childUpdatedCalls;

    node2.addSamples(2, 0.5);  // In this game instance it should be 0.0, but
                               // we need to test that value propagates
                               // correctly.
    assertFalse(node2.knowExactValue());
    assertEquals(2, node2.getSamples());
    assertEquals(2, node2.getTotalSamples());
    assertEquals(0.5, node2.getValue(), 1E-8);

    assertFalse(root.knowExactValue());
    assertEquals(3, root.getSamples());
    assertEquals(3, root.getTotalSamples());
    assertEquals(2.0/3.0, root.getValue(), 1E-8);

    // assertTrue(rootSelector.childUpdatedCalls > oldChildUpdates);

    // Test sampling node 1.

    rootSelector.selectResultState = state1;
    Node<TreeGame> node1 = root.selectChildOrAddPending(2);
    assertEquals(3, root.getSamples());
    assertEquals(5, root.getTotalSamples());
    assertEquals(0.4, root.getValue(), 1E-8);

    assertNotNull(node1);
    assertEquals(state1, node1.getState());
    assertEquals(root, node1.getParent());
    assertEquals(rootState.getMoveToNode(1), node1.getMove());
    assertEquals(0, node1.getSamples());
    assertEquals(0, node1.getTotalSamples());

    // oldChildUpdates = rootSelector.childUpdatedCalls;

    Node<TreeGame> void_node = node1.selectChildOrAddPending(2);
    assertEquals(Node.KNOW_EXACT_VALUE, void_node);
    assertTrue(node1.knowExactValue());
    assertEquals(1.0, node1.getValue(), 1E-8);
    assertEquals(2, node1.getSamples());
    assertEquals(2, node1.getTotalSamples());

    // assertTrue(rootSelector.childUpdatedCalls > oldChildUpdates);

    assertEquals(5, root.getSamples());
    assertEquals(5, root.getTotalSamples());
    if (root.knowExactValue()) {
      assertEquals(1.0, root.getValue(), 1E-8);
    } else {
      assertEquals(0.8, root.getValue(), 1E-8);
    }
  }

  @Test(timeout=50)
  public void testExactValues() {
    TestSelector rootSelector = new TestSelector();
    TreeGameState rootState = TreeGameInstances.GAME0.newGame();

    Node<TreeGame> root = new Node<TreeGame>(
        null, rootState, null, rootSelector, new NodeContext(true));

    TreeGameState state1 = rootState.play(rootState.getMoveToNode(1));
    TreeGameState state2 = rootState.play(rootState.getMoveToNode(2));
    TreeGameState state3 = state2.play(state2.getMoveToNode(3));

    rootSelector.shouldCreateChildrenResult = true;
    rootSelector.selectResultState = state2;
    Node<TreeGame> node2 = root.selectChildOrAddPending(2);

    assertNotNull(node2);
    assertEquals(state2, node2.getState());
    assertFalse(node2.knowExactValue());

    TestSelector selector2 = rootSelector.getChildSelector(node2);
    selector2.shouldCreateChildrenResult = true;
    selector2.selectResultState = state3;

    assertEquals(Node.KNOW_EXACT_VALUE, node2.selectChildOrAddPending(2));
    assertTrue(node2.knowExactValue());
    assertEquals(0.0, node2.getValue(), 1E-8);
    // assertFalse(root.knowExactValue());
    // assertEquals(0.0, root.getValue(), 1E-8);
    //
    // rootSelector.selectResultState = state1;
    // Node<TreeGame> node1 = root.selectChildOrAddPending(1);
    //
    // assertNotNull(node1);
    // assertEquals(state1, node1.getState());
    //
    // assertEquals(Node.KNOW_EXACT_VALUE, node3.selectChildOrAddPending(1));

    // assertTrue(node1.knowExactValue());
    assertTrue(root.knowExactValue());
    assertEquals(1.0, root.getValue(), 1E-8);
  }
}
