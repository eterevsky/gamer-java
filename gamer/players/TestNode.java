package gamer.players;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import gamer.def.GameStatus;
import gamer.treegame.TreeGame;
import gamer.treegame.TreeGameInstances;

import org.junit.Test;

public final class TestNode {

  @Test(timeout=10)
  public void testNoChildren() {
    Node<TreeGame> node = new Node<TreeGame>(
        null,
        TreeGameInstances.GAME0.newGame(),
        null,
        new LeafSelector<TreeGame>());

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

    node.addSamples(3, 0.33333333333333333333333333333333);
    assertEquals(10, node.getSamples());
    assertEquals(10, node.getTotalSamples());
    assertEquals(0.4, node.getValue(), 1E-8);
  }
}
