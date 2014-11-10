package gamer.players;

import static gamer.def.GameStatus.WIN;
import static gamer.def.GameStatus.LOSS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import gamer.def.GameState;
import gamer.def.GameStatus;
import gamer.def.Helper;
import gamer.treegame.TreeGame;
import gamer.treegame.TreeGameInstances;
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

      Node<TreeGame> node1 = root.selectChildOrAddPending(10);
      node1.selectChildOrAddPending(10);
      node1.addSamples(10, 0.8);
      Node<TreeGame> node2 = root.selectChildOrAddPending(12);
      node2.selectChildOrAddPending(12);
      node2.addSamples(12, 0.9);
      Node<TreeGame> node3 = root.selectChildOrAddPending(5);
      node3.selectChildOrAddPending(5);
      node3.addSamples(5, 0.2);
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

    protected MockSampler newSampler(
        Node<TreeGame> root, long finishTime, long samplesLimit,
        int samplesBatch, Random random) {
      assertEquals(this.root, root);
      assertEquals(123456L + 239, finishTime);
      assertEquals(234, samplesLimit);
      assertEquals(12, samplesBatch);
      assertNull(random);
      return sampler;
    }
  }

  @Test(timeout=500)
  public void selectMove() {
    TreeGame game = TreeGame.newBuilder().setRoot(0)
        .addMove(0, 1).addMove(0, 2).addMove(0, 3)
        .toGame();

    Node<TreeGame> root = new Node<TreeGame>(
        null, game.newGame(), null, new NaiveMonteCarlo.Selector<TreeGame>(),
        new NodeContext<TreeGame>());

    MockSampler sampler = new MockSampler(root);
    MockPlayer player = new MockPlayer(root, sampler);
    player.setTimeout(239).setSamplesLimit(234).setSamplesBatch(12);
    TreeGameMove move = (TreeGameMove) player.selectMove(root.getState());
    assertEquals("-> 2", move.toString());
  }

  private class MockHelper implements Helper<TreeGame> {
    public Helper.Result evaluate(GameState<TreeGame> stateI) {
      TreeGameState state = (TreeGameState) stateI;
      switch (state.getId()) {
        case 4: return new Helper.Result(WIN, 3);
        case 5: return new Helper.Result(WIN, 1);
        case 7: return new Helper.Result(WIN, 2);
        case 8: return new Helper.Result(WIN, 6);
        case 9: return new Helper.Result(LOSS, 1);
        case 10: return new Helper.Result(WIN, 1);
        default: return null;
      }
    }
  }

  @Test
  public void useHelper() {
    TreeGame game = TreeGameInstances.GAME4;
    MonteCarloUct<TreeGame> player = new MonteCarloUct<TreeGame>();
    player.setHelper(new MockHelper());
    player.setSamplesLimit(100);
    player.setFindExact(true);

    TreeGameState state = game.newGame();
    state = state.play(player.selectMove(state));
    assertEquals(2, state.getId());
    state = state.play(player.selectMove(state));
    assertEquals(4, state.getId());
    state = state.play(player.selectMove(state));
    assertEquals(7, state.getId());
    state = state.play(player.selectMove(state));
    assertEquals(10, state.getId());
  }
}
