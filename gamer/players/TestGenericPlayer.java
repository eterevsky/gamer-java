package gamer.players;

import static gamer.def.GameStatus.WIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import gamer.def.GameState;
import gamer.def.GameStatus;
import gamer.treegame.TreeGame;
import gamer.treegame.TreeGameMove;
import gamer.treegame.TreeGameState;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

public final class TestGenericPlayer {

  private class MockSampler extends Sampler<TreeGame> {
    boolean wasCalled = false;
    private Node<TreeGame> root;

    MockSampler(Node<TreeGame> root) {
      super(root, 123456L + 239, 234, 12, null);
      this.root = root;
    }

    public void run() {
      assertFalse(wasCalled);
      wasCalled = true;

      Node<TreeGame> node1 = null, node2 = null, node3 = null;
      for (Node<TreeGame> node : root.getChildren()) {
        switch (((TreeGameState)node.getState()).getId()) {
          case 1: node1 = node; break;
          case 2: node2 = node; break;
          case 3: node3 = node; break;
          default: throw new AssertionError();
        }
      }

      root.addPendingSamples(27);
      node1.addPendingSamples(10);
      node1.addSamples(10, 8.0);
      node2.addPendingSamples(12);
      node2.addSamples(12, 10.0);
      node3.addPendingSamples(5);
      node3.addSamples(5, 1.0);
    }
  }

  private class MockPlayer extends GenericPlayer<TreeGame> {
    private Node<TreeGame> root;
    private MockSampler sampler;
    MockPlayer(Node<TreeGame> root, MockSampler sampler) {
      this.root = root;
      this.sampler = sampler;
    }

    protected Node<TreeGame> getRoot(GameState<TreeGame> state) {
      return root;
    }

    protected long getCurrentTime() {
      return 123456L;
    }

    protected MockSampler getSampler(
        Node<TreeGame> root, long finishTime, long samplesLimit, int samplesBatch,
        Random random) {
      assertEquals(this.root, root);
      assertEquals(123456L + 239, finishTime);
      assertEquals(234, samplesLimit);
      assertEquals(12, samplesBatch);
      assertNull(random);
      return sampler;
    }
  }

  @Test(timeout=10)
  public void selectMove() {

    TreeGame game = TreeGame.newBuilder().setRoot(0)
        .addMove(0, 1).addMove(0, 2).addMove(0, 3)
        .toGame();

    NodeNaiveRoot<TreeGame> root = new NodeNaiveRoot<>(game.newGame());

    MockSampler sampler = new MockSampler(root);
    MockPlayer player = new MockPlayer(root, sampler);
    player.setTimeout(239).setSamplesLimit(234).setSamplesBatch(12);
    TreeGameMove move = (TreeGameMove) player.selectMove(root.getState());
    assertEquals("-> 2", move.toString());
  }
}
