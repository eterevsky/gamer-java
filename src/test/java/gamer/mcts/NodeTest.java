package gamer.mcts;

import gamer.treegame.TreeGameInstances;
import gamer.treegame.TreeGameMove;
import gamer.treegame.TreeGameState;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NodeTest {

  @Test
  public void testSamples() {
    // Initialize root
    TreeGameState state0 = TreeGameInstances.GAME0.newGame();
    Node<TreeGameState, TreeGameMove> node0 =
        new Node<>(new Node.Context<>(TreeGameInstances.GAME0), null, state0,
                   null);
    assertFalse(node0.hasChildren());
    assertFalse(node0.hasExactPayoff());
    assertEquals(0, node0.getTotalSamples());
//    assertEquals(-1, node0.getBiasedPayoff(), 1E-10);
//    assertEquals(0, node0.getBiasedVariance(node0.getBiasedPayoff()), 1E-10);
    assertNotNull(node0.toString());
    assertNotNull(node0.toString(state0));

    // Add 2 samples to root: -1, 1
    node0.addPendingSamples(2);
    assertEquals(2, node0.getTotalSamples());
    node0.addSamples(2, 0, 2);
    assertEquals(0, node0.getPayoff(), 1E-10);
//    assertEquals(-0.333, node0.getBiasedPayoff(), 0.001);
//    assertEquals(0.889, node0.getBiasedVariance(node0.getBiasedPayoff()),
//                 0.001);

    // Init children with 0 samples.
    node0.initChildren(state0);
    assertTrue(node0.hasChildren());
    String s = node0.toString(state0);
    assertTrue(s.split("\n").length == 4);

    // Check node 1
    Node<TreeGameState, TreeGameMove> node1 =
        node0.getChild(TreeGameInstances.GAME0.getMove(1));
    assertFalse(node1.hasChildren());
    assertTrue(node1.hasExactPayoff());
    assertEquals(0, node1.getTotalSamples());
    assertEquals(1, node1.getExactPayoff());
    assertEquals(1, node1.getPayoff(), 1E-10);
//    assertEquals(1, node1.getBiasedPayoff(), 1E-10);
//    assertEquals(0, node1.getBiasedVariance(node1.getBiasedPayoff()), 1E-10);

    // Check node 2
    Node<TreeGameState, TreeGameMove> node2 =
        node0.getChild(TreeGameInstances.GAME0.getMove(2));
    assertFalse(node2.hasChildren());
    assertFalse(node2.hasExactPayoff());
//    assertEquals(-1, node2.getBiasedPayoff(), 1E-10);
//    assertEquals(0, node2.getBiasedVariance(node1.getBiasedPayoff()), 1E-10);

    // Add 2 samples to node 1: exact 1, 1
    node0.addPendingSamples(2);
    node0.addSamples(2, 2, 2);
    node1.addExactSamples(2);
    assertEquals(4, node0.getTotalSamples());
    assertEquals(0, node0.getPendingSamples());
    assertEquals(2, node1.getTotalSamples());
    assertEquals(0, node1.getPendingSamples());
//    assertEquals(1, node1.getBiasedPayoff(), 1E-10);
//    assertEquals(0, node1.getBiasedVariance(node1.getBiasedPayoff()), 1E-10);

    // Add 2 samples to node 2: 0, 0
    node0.addPendingSamples(2);
    node0.addSamples(2, -2, 2);
    node2.addPendingSamples(2);
    node2.addSamples(2, -2, 2);
    assertEquals(6, node0.getTotalSamples());
    assertEquals(0, node0.getPendingSamples());
    assertEquals(2, node2.getTotalSamples());
    assertEquals(0, node2.getPendingSamples());
//    assertEquals(-1, node2.getBiasedPayoff(), 1E-10);
//    assertEquals(0, node2.getBiasedVariance(node2.getBiasedPayoff()), 1E-10);

    assertTrue(node1.getBiasedScore(2, false) >
               node2.getBiasedScore(2, false) + 0.001);
  }

  private void worker(
      Node<TreeGameState, TreeGameMove> node, TreeGameState state) {
    node.addPendingSamples(2);
    node.addSamples(2, 0, 2);
    node.addPendingSamples(2);
    node.addSamples(2, 0, 2);
    if (!node.hasChildren()) {
      node.initChildren(state);
    }
    assertEquals(2, node.getChildren().size());
    Node<TreeGameState, TreeGameMove> node1 =
        node.getChild(TreeGameInstances.GAME0.getMove(1));
    assertEquals(TreeGameInstances.GAME0.getMove(1), node1.getMove());
    assertTrue(node1.hasExactPayoff());
  }

  @Test
  public void testConcurrency() {
    TreeGameState state0 = TreeGameInstances.GAME0.newGame();
    Node<TreeGameState, TreeGameMove> node0 =
        new Node<>(new Node.Context<>(TreeGameInstances.GAME0), null, state0,
                   null);

    ExecutorService executor = Executors.newFixedThreadPool(128);
    List<Future<?>> tasks = new ArrayList<>();
    for (int i = 0; i < 128; i++) {
      tasks.add(executor.submit(() -> worker(node0, state0)));
    }

    for (Future<?> task : tasks) {
      try {
        task.get();
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
    executor.shutdown();

    assertTrue(node0.hasChildren());
    assertEquals(0, node0.getPendingSamples());
    assertEquals(512, node0.getTotalSamples());
    assertEquals(0, node0.getPayoffSum());
    assertEquals(512, node0.getPayoffSquaresSum());
  }
}