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
    Node<TreeGame> node = new Node(null,
                                   TreeGameInstances.GAME0.newGame(),
                                   null,
                                   new LeafSelector());

    assertNull(node.getParent());
    assertNull(node.getMove());
    assertEquals(TreeGameInstances.GAME0.newGame(), node.getState());
    assertEquals(0, node.getSamples());
    assertEquals(0, node.getTotalSamples());
  }
}
